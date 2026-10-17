import React, { useState, useEffect } from 'react';
import {
  Modal,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  SafeAreaView,
  Image,
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { CATEGORIES } from '../constants/categories';
import { scanReceiptOffline } from '../services/receiptScanner';

export const AddExpenseModal = ({
  visible,
  onClose,
  onSave,
  currency = '$',
  initialExpense = null,
}) => {
  const [amount, setAmount] = useState('');
  const [categoryId, setCategoryId] = useState('groceries');
  const [note, setNote] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [receiptUri, setReceiptUri] = useState(null);
  const [isScanning, setIsScanning] = useState(false);
  const [detectedCandidates, setDetectedCandidates] = useState([]);
  const [scanMessage, setScanMessage] = useState('');

  useEffect(() => {
    if (initialExpense) {
      setAmount(initialExpense.amount ? initialExpense.amount.toString() : '');
      setCategoryId(initialExpense.categoryId || 'groceries');
      setNote(initialExpense.note || '');
      setDate(initialExpense.date || new Date().toISOString().split('T')[0]);
      setReceiptUri(initialExpense.receiptUri || null);
    } else {
      setAmount('');
      setCategoryId('groceries');
      setNote('');
      setDate(new Date().toISOString().split('T')[0]);
      setReceiptUri(null);
      setDetectedCandidates([]);
      setScanMessage('');
    }
  }, [initialExpense, visible]);

  const handleTakePhoto = async () => {
    try {
      const { status } = await ImagePicker.requestCameraPermissionsAsync();
      if (status !== 'granted') {
        Alert.alert(
          'Camera Permission',
          'Camera access is needed strictly offline to photograph your paper bills and receipts.'
        );
        return;
      }

      const result = await ImagePicker.launchCameraAsync({
        mediaTypes: ['images'],
        allowsEditing: true,
        quality: 0.8,
      });

      if (!result.canceled && result.assets && result.assets.length > 0) {
        processSelectedImage(result.assets[0].uri);
      }
    } catch (err) {
      console.warn('Error taking photo:', err);
    }
  };

  const handlePickGallery = async () => {
    try {
      const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (status !== 'granted') {
        Alert.alert(
          'Photo Library Permission',
          'Gallery access is needed strictly offline to scan receipt screenshots.'
        );
        return;
      }

      const result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ['images'],
        allowsEditing: true,
        quality: 0.8,
      });

      if (!result.canceled && result.assets && result.assets.length > 0) {
        processSelectedImage(result.assets[0].uri);
      }
    } catch (err) {
      console.warn('Error picking image:', err);
    }
  };

  const processSelectedImage = async (uri) => {
    setReceiptUri(uri);
    setIsScanning(true);
    setScanMessage('Scanning bill completely offline...');

    try {
      const scanResult = await scanReceiptOffline(uri);
      if (scanResult.success) {
        if (scanResult.detectedAmount && (!amount || amount === '0')) {
          setAmount(scanResult.detectedAmount.toString());
        }
        if (scanResult.detectedDate) {
          setDate(scanResult.detectedDate);
        }
        if (scanResult.detectedMerchant && !note) {
          setNote(scanResult.detectedMerchant);
        }
        setDetectedCandidates(scanResult.candidateAmounts || []);
        setScanMessage('✨ Offline scan complete! Amount & date detected.');
      }
    } catch (error) {
      console.warn('Scan failed:', error);
      setScanMessage('Receipt attached. Enter amount manually if needed.');
    } finally {
      setIsScanning(false);
    }
  };

  const handleSelectCandidateAmount = (val) => {
    setAmount(val.toString());
  };

  const handleQuickDate = (type) => {
    const today = new Date();
    if (type === 'today') {
      setDate(today.toISOString().split('T')[0]);
    } else if (type === 'yesterday') {
      const yest = new Date(today.getTime() - 86400000);
      setDate(yest.toISOString().split('T')[0]);
    }
  };

  const handleSave = () => {
    const parsedAmount = parseFloat(amount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      Alert.alert('Invalid Amount', 'Please enter a valid expense amount.');
      return;
    }

    onSave({
      id: initialExpense ? initialExpense.id : undefined,
      amount: parsedAmount,
      categoryId,
      note: note.trim(),
      date,
      receiptUri,
    });

    onClose();
  };

  return (
    <Modal
      visible={visible}
      animationType="slide"
      presentationStyle="pageSheet"
      onRequestClose={onClose}
    >
      <SafeAreaView style={styles.safeArea}>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
          style={styles.container}
        >
          <View style={styles.topBar}>
            <TouchableOpacity
              onPress={onClose}
              style={styles.cancelButton}
              activeOpacity={0.7}
            >
              <Text style={styles.cancelText}>Cancel</Text>
            </TouchableOpacity>

            <Text style={styles.headerTitle}>
              {initialExpense ? 'Edit Expense' : 'Add Expense'}
            </Text>

            <TouchableOpacity
              onPress={handleSave}
              style={styles.saveHeaderButton}
              activeOpacity={0.7}
            >
              <Text style={styles.saveHeaderText}>Save</Text>
            </TouchableOpacity>
          </View>

          <ScrollView
            contentContainerStyle={styles.scrollContent}
            showsVerticalScrollIndicator={false}
            keyboardShouldPersistTaps="handled"
          >
            <View style={styles.amountInputBlock}>
              <Text style={styles.inputSectionLabel}>ENTER AMOUNT</Text>
              <View style={styles.amountInputRow}>
                <Text style={styles.amountCurrency}>{currency}</Text>
                <TextInput
                  style={styles.amountTextInput}
                  value={amount}
                  onChangeText={setAmount}
                  placeholder="0.00"
                  placeholderTextColor={THEME.colors.textMuted}
                  keyboardType="decimal-pad"
                  autoFocus={!initialExpense}
                />
              </View>

              {detectedCandidates.length > 0 && (
                <View style={styles.candidatesRow}>
                  <Text style={styles.candidateLabel}>Detected in receipt:</Text>
                  {detectedCandidates.map((val, idx) => (
                    <TouchableOpacity
                      key={idx}
                      style={[
                        styles.candidatePill,
                        amount === val.toString() && styles.candidatePillActive,
                      ]}
                      onPress={() => handleSelectCandidateAmount(val)}
                    >
                      <Text
                        style={[
                          styles.candidatePillText,
                          amount === val.toString() && styles.candidatePillTextActive,
                        ]}
                      >
                        {currency}
                        {val.toFixed(2)}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            </View>

            <View style={styles.section}>
              <Text style={styles.inputSectionLabel}>BILL / SCREENSHOT (100% OFFLINE SCAN)</Text>

              {receiptUri ? (
                <View style={styles.receiptPreviewBox}>
                  <Image source={{ uri: receiptUri }} style={styles.receiptPreviewImage} />
                  <View style={styles.receiptPreviewDetails}>
                    <View style={styles.offlineBadge}>
                      <MaterialIcons name="cloud-off" size={13} color={THEME.colors.primary} />
                      <Text style={styles.offlineBadgeText}>Saved Offline</Text>
                    </View>
                    {isScanning ? (
                      <View style={styles.scanningWrap}>
                        <ActivityIndicator size="small" color={THEME.colors.primary} />
                        <Text style={styles.scanningText}>{scanMessage}</Text>
                      </View>
                    ) : (
                      <Text style={styles.scanSuccessText}>{scanMessage || 'Receipt Attached'}</Text>
                    )}

                    <View style={styles.receiptActionButtons}>
                      <TouchableOpacity
                        style={styles.receiptChangeBtn}
                        onPress={handlePickGallery}
                      >
                        <Text style={styles.receiptChangeBtnText}>Change</Text>
                      </TouchableOpacity>
                      <TouchableOpacity
                        style={styles.receiptRemoveBtn}
                        onPress={() => {
                          setReceiptUri(null);
                          setDetectedCandidates([]);
                          setScanMessage('');
                        }}
                      >
                        <Text style={styles.receiptRemoveBtnText}>Remove</Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                </View>
              ) : (
                <View style={styles.photoActionsRow}>
                  <TouchableOpacity
                    style={styles.photoActionButton}
                    onPress={handleTakePhoto}
                    activeOpacity={0.7}
                  >
                    <MaterialIcons name="photo-camera" size={20} color={THEME.colors.primary} />
                    <Text style={styles.photoActionText}>Take Photo</Text>
                  </TouchableOpacity>

                  <TouchableOpacity
                    style={styles.photoActionButton}
                    onPress={handlePickGallery}
                    activeOpacity={0.7}
                  >
                    <MaterialIcons name="image" size={20} color={THEME.colors.primary} />
                    <Text style={styles.photoActionText}>Pick Screenshot</Text>
                  </TouchableOpacity>
                </View>
              )}
            </View>

            <View style={styles.section}>
              <Text style={styles.inputSectionLabel}>CATEGORY</Text>
              <View style={styles.categoriesGrid}>
                {CATEGORIES.map((cat) => {
                  const isSelected = categoryId === cat.id;
                  return (
                    <TouchableOpacity
                      key={cat.id}
                      style={[
                        styles.categoryChip,
                        isSelected && {
                          backgroundColor: cat.bgColor,
                          borderColor: cat.color,
                        },
                      ]}
                      onPress={() => setCategoryId(cat.id)}
                      activeOpacity={0.7}
                    >
                      <MaterialIcons
                        name={cat.icon}
                        size={18}
                        color={isSelected ? cat.color : THEME.colors.textSecondary}
                      />
                      <Text
                        style={[
                          styles.categoryChipText,
                          isSelected && { color: cat.color, fontWeight: '600' },
                        ]}
                      >
                        {cat.name}
                      </Text>
                    </TouchableOpacity>
                  );
                })}
              </View>
            </View>

            <View style={styles.section}>
              <Text style={styles.inputSectionLabel}>NOTE / DESCRIPTION</Text>
              <TextInput
                style={styles.noteInput}
                value={note}
                onChangeText={setNote}
                placeholder="e.g. Weekly grocery stock up & tea"
                placeholderTextColor={THEME.colors.textMuted}
                maxLength={100}
              />
            </View>

            <View style={styles.section}>
              <Text style={styles.inputSectionLabel}>DATE (YYYY-MM-DD)</Text>
              <View style={styles.dateRow}>
                <TextInput
                  style={styles.dateInput}
                  value={date}
                  onChangeText={setDate}
                  placeholder="YYYY-MM-DD"
                  placeholderTextColor={THEME.colors.textMuted}
                  maxLength={10}
                />
                <View style={styles.quickDateChips}>
                  <TouchableOpacity
                    style={[
                      styles.quickDateChip,
                      date === new Date().toISOString().split('T')[0] &&
                        styles.quickDateChipActive,
                    ]}
                    onPress={() => handleQuickDate('today')}
                  >
                    <Text style={styles.quickDateText}>Today</Text>
                  </TouchableOpacity>
                  <TouchableOpacity
                    style={styles.quickDateChip}
                    onPress={() => handleQuickDate('yesterday')}
                  >
                    <Text style={styles.quickDateText}>Yesterday</Text>
                  </TouchableOpacity>
                </View>
              </View>
            </View>

            <TouchableOpacity
              style={styles.mainSaveButton}
              onPress={handleSave}
              activeOpacity={0.8}
            >
              <MaterialIcons name="check" size={20} color="#FFFFFF" />
              <Text style={styles.mainSaveButtonText}>
                {initialExpense ? 'Update Record' : 'Save Expense'}
              </Text>
            </TouchableOpacity>
          </ScrollView>
        </KeyboardAvoidingView>
      </SafeAreaView>
    </Modal>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: THEME.colors.background,
  },
  container: {
    flex: 1,
  },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: THEME.spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: THEME.colors.border,
    backgroundColor: THEME.colors.surface,
  },
  cancelButton: {
    paddingVertical: 6,
    paddingHorizontal: 4,
  },
  cancelText: {
    fontSize: 16,
    color: THEME.colors.textSecondary,
  },
  headerTitle: {
    fontSize: 17,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
  },
  saveHeaderButton: {
    paddingVertical: 6,
    paddingHorizontal: 12,
    backgroundColor: THEME.colors.primary,
    borderRadius: THEME.borderRadius.round,
  },
  saveHeaderText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  scrollContent: {
    padding: THEME.spacing.lg,
    paddingBottom: 40,
  },
  amountInputBlock: {
    backgroundColor: THEME.colors.surface,
    padding: THEME.spacing.lg,
    borderRadius: THEME.borderRadius.xl,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    marginBottom: THEME.spacing.lg,
    alignItems: 'center',
    ...THEME.shadows.card,
  },
  inputSectionLabel: {
    fontSize: 11,
    fontWeight: '600',
    color: THEME.colors.textMuted,
    letterSpacing: 0.6,
    marginBottom: 8,
  },
  amountInputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: 4,
  },
  amountCurrency: {
    fontSize: 32,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginRight: 4,
  },
  amountTextInput: {
    fontSize: 42,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    minWidth: 140,
    textAlign: 'center',
    letterSpacing: -1,
  },
  candidatesRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    marginTop: 10,
    gap: 6,
  },
  candidateLabel: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    marginRight: 4,
  },
  candidatePill: {
    backgroundColor: THEME.colors.surfaceSecondary,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: THEME.borderRadius.round,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  candidatePillActive: {
    backgroundColor: THEME.colors.primaryLight,
    borderColor: THEME.colors.primary,
  },
  candidatePillText: {
    fontSize: 12,
    fontWeight: '500',
    color: THEME.colors.textSecondary,
  },
  candidatePillTextActive: {
    color: THEME.colors.primary,
    fontWeight: '700',
  },
  section: {
    marginBottom: THEME.spacing.lg,
  },
  photoActionsRow: {
    flexDirection: 'row',
    gap: 12,
  },
  photoActionButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: THEME.colors.surface,
    paddingVertical: 14,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    gap: 8,
    ...THEME.shadows.card,
  },
  photoActionText: {
    fontSize: 14,
    fontWeight: '600',
    color: THEME.colors.primary,
  },
  receiptPreviewBox: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.surface,
    padding: 12,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    alignItems: 'center',
    ...THEME.shadows.card,
  },
  receiptPreviewImage: {
    width: 64,
    height: 64,
    borderRadius: 8,
    marginRight: 12,
    backgroundColor: THEME.colors.surfaceSecondary,
  },
  receiptPreviewDetails: {
    flex: 1,
  },
  offlineBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    marginBottom: 4,
  },
  offlineBadgeText: {
    fontSize: 11,
    color: THEME.colors.primary,
    fontWeight: '600',
  },
  scanningWrap: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    marginVertical: 2,
  },
  scanningText: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
  },
  scanSuccessText: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    marginBottom: 6,
  },
  receiptActionButtons: {
    flexDirection: 'row',
    gap: 10,
  },
  receiptChangeBtn: {
    paddingVertical: 4,
    paddingHorizontal: 8,
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: 6,
  },
  receiptChangeBtnText: {
    fontSize: 11,
    fontWeight: '600',
    color: THEME.colors.textSecondary,
  },
  receiptRemoveBtn: {
    paddingVertical: 4,
    paddingHorizontal: 8,
    backgroundColor: THEME.colors.dangerBg,
    borderRadius: 6,
  },
  receiptRemoveBtnText: {
    fontSize: 11,
    fontWeight: '600',
    color: THEME.colors.danger,
  },
  categoriesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  categoryChip: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.surface,
    paddingHorizontal: 12,
    paddingVertical: 9,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    gap: 6,
  },
  categoryChipText: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
  },
  noteInput: {
    backgroundColor: THEME.colors.surface,
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    fontSize: 15,
    color: THEME.colors.textPrimary,
  },
  dateRow: {
    flexDirection: 'row',
    gap: 10,
    alignItems: 'center',
  },
  dateInput: {
    flex: 1,
    backgroundColor: THEME.colors.surface,
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    fontSize: 15,
    color: THEME.colors.textPrimary,
  },
  quickDateChips: {
    flexDirection: 'row',
    gap: 6,
  },
  quickDateChip: {
    paddingHorizontal: 12,
    paddingVertical: 12,
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  quickDateChipActive: {
    backgroundColor: THEME.colors.primaryLight,
    borderColor: THEME.colors.primary,
  },
  quickDateText: {
    fontSize: 13,
    fontWeight: '500',
    color: THEME.colors.textSecondary,
  },
  mainSaveButton: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.primary,
    paddingVertical: 16,
    borderRadius: THEME.borderRadius.xl,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 8,
    marginTop: 10,
    ...THEME.shadows.floating,
  },
  mainSaveButtonText: {
    fontSize: 16,
    fontWeight: '700',
    color: '#FFFFFF',
    letterSpacing: 0.3,
  },
});
