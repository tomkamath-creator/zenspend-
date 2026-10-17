import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Switch,
  TextInput,
  Alert,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { useExpense } from '../context/ExpenseContext';
import { checkBiometricsAvailable } from '../services/authService';
import { Card } from '../components/Card';
import { SetBudgetModal } from '../components/SetBudgetModal';

const CURRENCIES = [
  { symbol: '$', label: 'USD / Dollar ($)' },
  { symbol: '€', label: 'EUR / Euro (€)' },
  { symbol: '£', label: 'GBP / British Pound (£)' },
  { symbol: '₹', label: 'INR / Indian Rupee (₹)' },
  { symbol: '¥', label: 'JPY / CNY (¥)' },
  { symbol: 'A$', label: 'AUD / Australian ($)' },
  { symbol: 'C$', label: 'CAD / Canadian ($)' },
];

export const SettingsScreen = () => {
  const {
    budget,
    currency,
    lockSettings,
    updateBudget,
    updateCurrency,
    updateLockSettings,
    resetAllExpenses,
  } = useExpense();

  const [isBudgetModalVisible, setIsBudgetModalVisible] = useState(false);
  const [biometricInfo, setBiometricInfo] = useState({ available: false, biometricType: 'Biometrics' });
  const [isPinModalVisible, setIsPinModalVisible] = useState(false);
  const [pinInput, setPinInput] = useState('');

  useEffect(() => {
    checkBiometricsAvailable().then(setBiometricInfo);
  }, []);

  const handleToggleLock = (enabled) => {
    if (enabled && !lockSettings.pin) {
      setIsPinModalVisible(true);
    } else {
      updateLockSettings({
        ...lockSettings,
        isEnabled: enabled,
      });
    }
  };

  const handleSavePin = () => {
    if (pinInput.length !== 4) {
      Alert.alert('Invalid PIN', 'Please enter a 4-digit numeric PIN.');
      return;
    }

    updateLockSettings({
      isEnabled: true,
      pin: pinInput,
      useBiometrics: biometricInfo.available,
    });

    setIsPinModalVisible(false);
    setPinInput('');
    Alert.alert('App Lock Activated', `SereneSpend is now protected by ${biometricInfo.biometricType} and your 4-digit PIN.`);
  };

  const handleResetData = () => {
    Alert.alert(
      'Reset All Records',
      'This will clear your local expenses and restore peaceful default starter data. This cannot be undone.',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Reset',
          style: 'destructive',
          onPress: async () => {
            await resetAllExpenses();
            Alert.alert('Reset Complete', 'Your data has been reset to starter values.');
          },
        },
      ]
    );
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.title}>Settings & Privacy</Text>
          <Text style={styles.subtitle}>
            Manage budget limits, currency, and biometric privacy.
          </Text>
        </View>

        <Text style={styles.groupTitle}>BUDGET & CURRENCY</Text>
        <Card style={styles.card}>
          <TouchableOpacity
            style={styles.settingRow}
            onPress={() => setIsBudgetModalVisible(true)}
            activeOpacity={0.7}
          >
            <View style={styles.settingLeft}>
              <View style={[styles.iconCircle, { backgroundColor: THEME.colors.primaryLight }]}>
                <MaterialIcons name="account-balance-wallet" size={20} color={THEME.colors.primary} />
              </View>
              <View>
                <Text style={styles.settingTitle}>Monthly Budget</Text>
                <Text style={styles.settingSubtitle}>
                  Current: {currency}
                  {budget.toFixed(2)}
                </Text>
              </View>
            </View>
            <MaterialIcons name="chevron-right" size={22} color={THEME.colors.textMuted} />
          </TouchableOpacity>

          <View style={styles.divider} />

          <View style={styles.currencySection}>
            <Text style={styles.innerLabel}>Default Currency Symbol</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.currencyScroll}>
              {CURRENCIES.map((c) => (
                <TouchableOpacity
                  key={c.symbol}
                  style={[
                    styles.currencyChip,
                    currency === c.symbol && styles.currencyChipActive,
                  ]}
                  onPress={() => updateCurrency(c.symbol)}
                >
                  <Text
                    style={[
                      styles.currencyChipText,
                      currency === c.symbol && styles.currencyChipTextActive,
                    ]}
                  >
                    {c.symbol} {c.label.split(' ')[0]}
                  </Text>
                </TouchableOpacity>
              ))}
            </ScrollView>
          </View>
        </Card>

        <Text style={styles.groupTitle}>SECURITY & PRIVACY</Text>
        <Card style={styles.card}>
          <View style={styles.settingRow}>
            <View style={styles.settingLeft}>
              <View style={[styles.iconCircle, { backgroundColor: '#F0EFF8' }]}>
                <MaterialIcons name="lock" size={20} color={THEME.colors.catEntertainment} />
              </View>
              <View style={{ flex: 1, paddingRight: 8 }}>
                <Text style={styles.settingTitle}>App Lock (Biometrics & PIN)</Text>
                <Text style={styles.settingSubtitle}>
                  {biometricInfo.available
                    ? `Protect with ${biometricInfo.biometricType} or 4-digit PIN.`
                    : 'Protect with 4-digit PIN.'}
                </Text>
              </View>
            </View>
            <Switch
              value={lockSettings.isEnabled}
              onValueChange={handleToggleLock}
              trackColor={{ false: THEME.colors.border, true: THEME.colors.primary }}
              thumbColor="#FFFFFF"
            />
          </View>

          {lockSettings.isEnabled && (
            <>
              <View style={styles.divider} />
              <TouchableOpacity
                style={styles.settingRow}
                onPress={() => setIsPinModalVisible(true)}
              >
                <View style={styles.settingLeft}>
                  <View style={[styles.iconCircle, { backgroundColor: THEME.colors.surfaceSecondary }]}>
                    <MaterialIcons name="pin" size={20} color={THEME.colors.textSecondary} />
                  </View>
                  <View>
                    <Text style={styles.settingTitle}>Change 4-Digit PIN</Text>
                    <Text style={styles.settingSubtitle}>Update your fallback security PIN</Text>
                  </View>
                </View>
                <MaterialIcons name="chevron-right" size={22} color={THEME.colors.textMuted} />
              </TouchableOpacity>
            </>
          )}
        </Card>

        <Text style={styles.groupTitle}>DATA MANAGEMENT</Text>
        <Card style={styles.card}>
          <TouchableOpacity
            style={styles.settingRow}
            onPress={handleResetData}
            activeOpacity={0.7}
          >
            <View style={styles.settingLeft}>
              <View style={[styles.iconCircle, { backgroundColor: THEME.colors.dangerBg }]}>
                <MaterialIcons name="restore" size={20} color={THEME.colors.danger} />
              </View>
              <View>
                <Text style={[styles.settingTitle, { color: THEME.colors.danger }]}>
                  Reset to Starter Data
                </Text>
                <Text style={styles.settingSubtitle}>
                  Restore sample expenses and clean initial state
                </Text>
              </View>
            </View>
            <MaterialIcons name="chevron-right" size={22} color={THEME.colors.textMuted} />
          </TouchableOpacity>
        </Card>

        <View style={styles.aboutCard}>
          <Text style={styles.aboutBrand}>🌿 SereneSpend v1.0.0</Text>
          <Text style={styles.aboutDesc}>
            100% Free • Completely Offline • Zero Internet • Zero Cloud Fees
          </Text>
          <Text style={styles.aboutSub}>
            Your financial records never leave this device.
          </Text>
        </View>
      </ScrollView>

      <SetBudgetModal
        visible={isBudgetModalVisible}
        currentBudget={budget}
        currency={currency}
        onClose={() => setIsBudgetModalVisible(false)}
        onSave={updateBudget}
      />

      {isPinModalVisible && (
        <View style={styles.pinModalOverlay}>
          <View style={styles.pinModalContent}>
            <View style={styles.pinModalHeader}>
              <View style={styles.pinModalIconWrap}>
                <MaterialIcons name="lock-outline" size={24} color={THEME.colors.primary} />
              </View>
              <Text style={styles.pinModalTitle}>Set 4-Digit Security PIN</Text>
              <Text style={styles.pinModalSub}>
                Enter a 4-digit code to unlock the app when biometrics are unavailable.
              </Text>
            </View>

            <TextInput
              style={styles.pinInputField}
              value={pinInput}
              onChangeText={setPinInput}
              keyboardType="number-pad"
              maxLength={4}
              secureTextEntry
              autoFocus
              placeholder="••••"
              placeholderTextColor={THEME.colors.textMuted}
            />

            <View style={styles.pinModalActions}>
              <TouchableOpacity
                style={styles.pinCancelBtn}
                onPress={() => {
                  setIsPinModalVisible(false);
                  setPinInput('');
                }}
              >
                <Text style={styles.pinCancelText}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.pinSaveBtn}
                onPress={handleSavePin}
              >
                <Text style={styles.pinSaveText}>Save PIN</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: THEME.colors.background,
  },
  scrollContent: {
    padding: THEME.spacing.lg,
    paddingBottom: 90,
  },
  header: {
    marginBottom: THEME.spacing.lg,
  },
  title: {
    fontSize: 22,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
  },
  groupTitle: {
    fontSize: 11,
    fontWeight: '700',
    color: THEME.colors.textMuted,
    letterSpacing: 0.6,
    marginBottom: 8,
    marginTop: 10,
    paddingHorizontal: 2,
  },
  card: {
    padding: THEME.spacing.md,
    marginBottom: THEME.spacing.md,
  },
  settingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 6,
  },
  settingLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  iconCircle: {
    width: 38,
    height: 38,
    borderRadius: 19,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  settingTitle: {
    fontSize: 15,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginBottom: 2,
  },
  settingSubtitle: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
  },
  divider: {
    height: 1,
    backgroundColor: THEME.colors.borderSubtle,
    marginVertical: 10,
  },
  currencySection: {
    marginTop: 4,
  },
  innerLabel: {
    fontSize: 12,
    fontWeight: '600',
    color: THEME.colors.textSecondary,
    marginBottom: 8,
  },
  currencyScroll: {
    flexDirection: 'row',
  },
  currencyChip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: THEME.borderRadius.round,
    backgroundColor: THEME.colors.surfaceSecondary,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    marginRight: 8,
  },
  currencyChipActive: {
    backgroundColor: THEME.colors.primaryLight,
    borderColor: THEME.colors.primary,
  },
  currencyChipText: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    fontWeight: '500',
  },
  currencyChipTextActive: {
    color: THEME.colors.primary,
    fontWeight: '700',
  },
  aboutCard: {
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 20,
    padding: 16,
  },
  aboutBrand: {
    fontSize: 15,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 4,
  },
  aboutDesc: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    textAlign: 'center',
    marginBottom: 2,
  },
  aboutSub: {
    fontSize: 11,
    color: THEME.colors.textMuted,
  },
  pinModalOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(30, 44, 34, 0.4)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: THEME.spacing.lg,
    zIndex: 9999,
  },
  pinModalContent: {
    width: '100%',
    maxWidth: 320,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.borderRadius.xl,
    padding: THEME.spacing.xl,
    ...THEME.shadows.floating,
  },
  pinModalHeader: {
    alignItems: 'center',
    marginBottom: 16,
  },
  pinModalIconWrap: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: THEME.colors.primaryLight,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
  },
  pinModalTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 4,
  },
  pinModalSub: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    textAlign: 'center',
    lineHeight: 16,
  },
  pinInputField: {
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    paddingVertical: 12,
    textAlign: 'center',
    fontSize: 28,
    letterSpacing: 10,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 20,
  },
  pinModalActions: {
    flexDirection: 'row',
    gap: 10,
  },
  pinCancelBtn: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: THEME.borderRadius.lg,
    backgroundColor: THEME.colors.surfaceSecondary,
    alignItems: 'center',
  },
  pinCancelText: {
    fontSize: 14,
    fontWeight: '600',
    color: THEME.colors.textSecondary,
  },
  pinSaveBtn: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: THEME.borderRadius.lg,
    backgroundColor: THEME.colors.primary,
    alignItems: 'center',
  },
  pinSaveText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFFFFF',
  },
});
