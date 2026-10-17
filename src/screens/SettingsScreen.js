import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { useExpense } from '../context/ExpenseContext';
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
    updateBudget,
    updateCurrency,
    resetAllExpenses,
  } = useExpense();

  const [isBudgetModalVisible, setIsBudgetModalVisible] = useState(false);

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
          <Text style={styles.title}>Settings</Text>
          <Text style={styles.subtitle}>
            Manage budget limits, currency, and local data.
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
});
