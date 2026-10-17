import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { Card } from './Card';

export const BudgetCard = ({
  budget,
  totalSpentThisMonth,
  remainingBudget,
  safeDailyAllowance,
  remainingDays,
  currency = '$',
  onEditBudget,
}) => {
  const isOverBudget = remainingBudget < 0;
  const spentPercent = budget > 0 ? Math.min(100, Math.max(0, (totalSpentThisMonth / budget) * 100)) : 0;
  
  const progressColor = isOverBudget
    ? THEME.colors.danger
    : spentPercent > 80
    ? THEME.colors.warning
    : THEME.colors.primary;

  return (
    <Card style={styles.container}>
      <View style={styles.headerRow}>
        <View style={styles.titleWithIcon}>
          <View style={styles.leafIconContainer}>
            <MaterialIcons name="eco" size={18} color={THEME.colors.primary} />
          </View>
          <Text style={styles.headerTitle}>Monthly Budget</Text>
        </View>

        <TouchableOpacity
          style={styles.editButton}
          onPress={onEditBudget}
          activeOpacity={0.7}
        >
          <MaterialIcons name="edit" size={16} color={THEME.colors.primary} />
          <Text style={styles.editButtonText}>Set Budget</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.balanceContainer}>
        <Text style={styles.balanceLabel}>
          {isOverBudget ? 'Over Budget by' : 'Money Left to Spend'}
        </Text>
        <View style={styles.balanceAmountRow}>
          <Text style={[styles.currencySymbol, isOverBudget && styles.dangerText]}>
            {currency}
          </Text>
          <Text style={[styles.balanceValue, isOverBudget && styles.dangerText]}>
            {Math.abs(remainingBudget).toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}
          </Text>
        </View>
      </View>

      <View style={styles.progressBackground}>
        <View
          style={[
            styles.progressFill,
            { width: `${spentPercent}%`, backgroundColor: progressColor },
          ]}
        />
      </View>

      <View style={styles.breakdownRow}>
        <View>
          <Text style={styles.metaLabel}>Spent This Month</Text>
          <Text style={styles.metaValue}>
            {currency}
            {totalSpentThisMonth.toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}
          </Text>
        </View>

        <View style={styles.metaRight}>
          <Text style={styles.metaLabel}>Total Budget</Text>
          <Text style={styles.metaValue}>
            {currency}
            {budget.toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}
          </Text>
        </View>
      </View>

      <View
        style={[
          styles.allowanceContainer,
          isOverBudget && styles.allowanceDangerContainer,
        ]}
      >
        <View style={styles.allowanceIconWrap}>
          <MaterialIcons
            name={isOverBudget ? 'warning-amber' : 'spa'}
            size={18}
            color={isOverBudget ? THEME.colors.danger : THEME.colors.primary}
          />
        </View>
        <View style={styles.allowanceTextWrap}>
          <Text style={styles.allowanceTitle}>
            {isOverBudget ? 'Budget Reached' : 'Safe Daily Allowance'}
          </Text>
          <Text style={styles.allowanceSubtitle}>
            {isOverBudget
              ? 'Try to minimize non-essential purchases for the rest of this month.'
              : `Spend up to ${currency}${safeDailyAllowance.toFixed(
                  2
                )}/day for the next ${remainingDays} days to stay on track.`}
          </Text>
        </View>
      </View>
    </Card>
  );
};

const styles = StyleSheet.create({
  container: {
    marginBottom: THEME.spacing.lg,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: THEME.spacing.md,
  },
  titleWithIcon: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  leafIconContainer: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: THEME.colors.primaryLight,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
  },
  headerTitle: {
    fontSize: 15,
    fontWeight: '600',
    color: THEME.colors.textSecondary,
    letterSpacing: 0.2,
  },
  editButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.primaryLight,
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: THEME.borderRadius.round,
  },
  editButtonText: {
    fontSize: 12,
    fontWeight: '600',
    color: THEME.colors.primary,
    marginLeft: 4,
  },
  balanceContainer: {
    marginVertical: THEME.spacing.xs,
  },
  balanceLabel: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
    marginBottom: 4,
  },
  balanceAmountRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  currencySymbol: {
    fontSize: 24,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginRight: 2,
  },
  balanceValue: {
    fontSize: 34,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    letterSpacing: -1,
  },
  dangerText: {
    color: THEME.colors.danger,
  },
  progressBackground: {
    height: 10,
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: 5,
    overflow: 'hidden',
    marginTop: THEME.spacing.md,
    marginBottom: THEME.spacing.md,
  },
  progressFill: {
    height: '100%',
    borderRadius: 5,
  },
  breakdownRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.md,
    paddingHorizontal: 2,
  },
  metaLabel: {
    fontSize: 11,
    color: THEME.colors.textMuted,
    marginBottom: 2,
    textTransform: 'uppercase',
    letterSpacing: 0.4,
  },
  metaValue: {
    fontSize: 15,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
  },
  metaRight: {
    alignItems: 'flex-end',
  },
  allowanceContainer: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.primaryLight,
    borderRadius: THEME.borderRadius.lg,
    padding: THEME.spacing.md,
    alignItems: 'center',
  },
  allowanceDangerContainer: {
    backgroundColor: THEME.colors.dangerBg,
  },
  allowanceIconWrap: {
    marginRight: 10,
  },
  allowanceTextWrap: {
    flex: 1,
  },
  allowanceTitle: {
    fontSize: 13,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginBottom: 2,
  },
  allowanceSubtitle: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    lineHeight: 16,
  },
});
