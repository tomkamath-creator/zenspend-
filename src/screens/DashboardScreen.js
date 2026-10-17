import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { useExpense } from '../context/ExpenseContext';
import { BudgetCard } from '../components/BudgetCard';
import { MetricsRow } from '../components/MetricsRow';
import { ExpenseItem } from '../components/ExpenseItem';
import { SetBudgetModal } from '../components/SetBudgetModal';

export const DashboardScreen = ({
  onOpenAddExpense,
  onOpenEditExpense,
  onViewReceipt,
  onNavigateToExpenses,
}) => {
  const {
    expenses,
    budget,
    currency,
    metrics,
    updateBudget,
    deleteExpense,
  } = useExpense();

  const [isBudgetModalVisible, setIsBudgetModalVisible] = useState(false);
  const recentExpenses = expenses.slice(0, 5);

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.topHeader}>
          <View>
            <View style={styles.brandRow}>
              <Text style={styles.greetingTitle}>SereneSpend</Text>
              <View style={styles.vkBadge}>
                <Text style={styles.vkBadgeText}>by VK</Text>
              </View>
              <View style={styles.offlinePill}>
                <MaterialIcons name="cloud-off" size={11} color={THEME.colors.primary} />
                <Text style={styles.offlinePillText}>Offline</Text>
              </View>
            </View>
            <Text style={styles.greetingSubtitle}>Mindful, stress-free money tracking</Text>
          </View>

          <TouchableOpacity
            style={styles.quickAddBtn}
            onPress={onOpenAddExpense}
            activeOpacity={0.8}
          >
            <MaterialIcons name="add" size={20} color="#FFFFFF" />
            <Text style={styles.quickAddText}>Add Bill</Text>
          </TouchableOpacity>
        </View>

        <BudgetCard
          budget={budget}
          totalSpentThisMonth={metrics.totalThisMonth}
          remainingBudget={metrics.remainingBudget}
          safeDailyAllowance={metrics.safeDailyAllowance}
          remainingDays={metrics.remainingDays}
          currency={currency}
          onEditBudget={() => setIsBudgetModalVisible(true)}
        />

        <MetricsRow
          totalToday={metrics.totalToday}
          totalThisMonth={metrics.totalThisMonth}
          totalThisYear={metrics.totalThisYear}
          currency={currency}
        />

        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Recent Expenses</Text>
          {expenses.length > 5 && (
            <TouchableOpacity onPress={onNavigateToExpenses} activeOpacity={0.7}>
              <Text style={styles.viewAllText}>View All ({expenses.length})</Text>
            </TouchableOpacity>
          )}
        </View>

        {recentExpenses.length === 0 ? (
          <View style={styles.emptyStateCard}>
            <View style={styles.emptyIconCircle}>
              <MaterialIcons name="spa" size={32} color={THEME.colors.primary} />
            </View>
            <Text style={styles.emptyTitle}>No expenses recorded yet</Text>
            <Text style={styles.emptySubtitle}>
              Tap the "+ Add Bill" button to record an expense or scan a receipt offline.
            </Text>
          </View>
        ) : (
          recentExpenses.map((expense) => (
            <ExpenseItem
              key={expense.id}
              expense={expense}
              currency={currency}
              onPress={onOpenEditExpense}
              onViewReceipt={onViewReceipt}
              onDelete={deleteExpense}
            />
          ))
        )}
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
  topHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: THEME.spacing.lg,
    marginTop: THEME.spacing.xs,
  },
  brandRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  greetingTitle: {
    fontSize: 24,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    letterSpacing: -0.5,
  },
  vkBadge: {
    backgroundColor: THEME.colors.surfaceSecondary,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: THEME.borderRadius.round,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  vkBadgeText: {
    fontSize: 10,
    fontWeight: '700',
    color: THEME.colors.primary,
    letterSpacing: 0.5,
    textTransform: 'uppercase',
  },
  offlinePill: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.primaryLight,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: THEME.borderRadius.round,
    gap: 4,
  },
  offlinePillText: {
    fontSize: 10,
    fontWeight: '700',
    color: THEME.colors.primary,
    textTransform: 'uppercase',
    letterSpacing: 0.3,
  },
  greetingSubtitle: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
    marginTop: 2,
  },
  quickAddBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.primary,
    paddingHorizontal: 14,
    paddingVertical: 9,
    borderRadius: THEME.borderRadius.round,
    gap: 4,
    ...THEME.shadows.floating,
  },
  quickAddText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: THEME.spacing.md,
    marginTop: THEME.spacing.xs,
    paddingHorizontal: 2,
  },
  sectionTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  viewAllText: {
    fontSize: 13,
    fontWeight: '600',
    color: THEME.colors.primary,
  },
  emptyStateCard: {
    backgroundColor: THEME.colors.surface,
    padding: THEME.spacing.xxl,
    borderRadius: THEME.borderRadius.xl,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: THEME.spacing.md,
  },
  emptyIconCircle: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: THEME.colors.primaryLight,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  emptyTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginBottom: 4,
  },
  emptySubtitle: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
    textAlign: 'center',
    maxWidth: 260,
    lineHeight: 18,
  },
});
