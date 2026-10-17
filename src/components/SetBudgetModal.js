import React, { useState, useEffect } from 'react';
import {
  Modal,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';

export const SetBudgetModal = ({
  visible,
  currentBudget,
  currency = '$',
  onClose,
  onSave,
}) => {
  const [budgetInput, setBudgetInput] = useState('');

  useEffect(() => {
    if (visible) {
      setBudgetInput(currentBudget ? currentBudget.toString() : '1500');
    }
  }, [visible, currentBudget]);

  const handleSave = () => {
    const val = parseFloat(budgetInput);
    if (isNaN(val) || val <= 0) {
      Alert.alert('Invalid Budget', 'Please enter a valid monthly budget amount.');
      return;
    }
    onSave(val);
    onClose();
  };

  return (
    <Modal
      visible={visible}
      animationType="fade"
      transparent={true}
      onRequestClose={onClose}
    >
      <View style={styles.modalOverlay}>
        <View style={styles.modalContent}>
          <View style={styles.header}>
            <View style={styles.iconWrap}>
              <MaterialIcons name="account-balance-wallet" size={24} color={THEME.colors.primary} />
            </View>
            <Text style={styles.title}>Monthly Budget</Text>
            <Text style={styles.subtitle}>
              Set how much you plan to spend this month. SereneSpend calculates your daily allowance automatically.
            </Text>
          </View>

          <View style={styles.inputRow}>
            <Text style={styles.currency}>{currency}</Text>
            <TextInput
              style={styles.input}
              value={budgetInput}
              onChangeText={setBudgetInput}
              keyboardType="decimal-pad"
              autoFocus
              selectTextOnFocus
              placeholder="1500"
              placeholderTextColor={THEME.colors.textMuted}
            />
          </View>

          <View style={styles.presetsRow}>
            {[1000, 1500, 2000, 3000].map((preset) => (
              <TouchableOpacity
                key={preset}
                style={[
                  styles.presetChip,
                  budgetInput === preset.toString() && styles.presetChipActive,
                ]}
                onPress={() => setBudgetInput(preset.toString())}
              >
                <Text
                  style={[
                    styles.presetText,
                    budgetInput === preset.toString() && styles.presetTextActive,
                  ]}
                >
                  {currency}
                  {preset}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          <View style={styles.actionsRow}>
            <TouchableOpacity
              style={styles.cancelButton}
              onPress={onClose}
              activeOpacity={0.7}
            >
              <Text style={styles.cancelText}>Cancel</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.saveButton}
              onPress={handleSave}
              activeOpacity={0.8}
            >
              <Text style={styles.saveText}>Save Budget</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(30, 44, 34, 0.4)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: THEME.spacing.lg,
  },
  modalContent: {
    width: '100%',
    maxWidth: 340,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.borderRadius.xl,
    padding: THEME.spacing.xl,
    ...THEME.shadows.floating,
  },
  header: {
    alignItems: 'center',
    marginBottom: THEME.spacing.lg,
  },
  iconWrap: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: THEME.colors.primaryLight,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    textAlign: 'center',
    lineHeight: 16,
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: THEME.borderRadius.lg,
    paddingVertical: 12,
    paddingHorizontal: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  currency: {
    fontSize: 24,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginRight: 6,
  },
  input: {
    fontSize: 28,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    minWidth: 120,
    textAlign: 'center',
  },
  presetsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.xl,
  },
  presetChip: {
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: THEME.borderRadius.round,
    backgroundColor: THEME.colors.surfaceSecondary,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  presetChipActive: {
    backgroundColor: THEME.colors.primaryLight,
    borderColor: THEME.colors.primary,
  },
  presetText: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    fontWeight: '500',
  },
  presetTextActive: {
    color: THEME.colors.primary,
    fontWeight: '700',
  },
  actionsRow: {
    flexDirection: 'row',
    gap: 10,
  },
  cancelButton: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: THEME.borderRadius.lg,
    backgroundColor: THEME.colors.surfaceSecondary,
    alignItems: 'center',
  },
  cancelText: {
    fontSize: 14,
    fontWeight: '600',
    color: THEME.colors.textSecondary,
  },
  saveButton: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: THEME.borderRadius.lg,
    backgroundColor: THEME.colors.primary,
    alignItems: 'center',
  },
  saveText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFFFFF',
  },
});
