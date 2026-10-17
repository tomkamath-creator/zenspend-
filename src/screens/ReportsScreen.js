import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
  ActivityIndicator,
  Alert,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { getCategoryById } from '../constants/categories';
import { useExpense } from '../context/ExpenseContext';
import { exportExpensesToCSV } from '../services/csvService';
import { Card } from '../components/Card';

export const ReportsScreen = () => {
  const { expenses, currency, metrics } = useExpense();
  const [isExportingCSV, setIsExportingCSV] = useState(false);

  const now = new Date();
  const monthName = now.toLocaleString('default', { month: 'long' });
  const year = now.getFullYear();

  const handleExportCSV = async () => {
    if (expenses.length === 0) {
      Alert.alert('No Records', 'Add some expenses before exporting a spreadsheet.');
      return;
    }

    setIsExportingCSV(true);
    try {
      await exportExpensesToCSV(expenses, currency);
    } catch (error) {
      Alert.alert('Export Failed', 'Could not export CSV file.');
    } finally {
      setIsExportingCSV(false);
    }
  };

  const categoryStats = Object.entries(metrics.categoryTotals || {})
    .sort(([, a], [, b]) => b - a)
    .map(([catId, total]) => {
      const cat = getCategoryById(catId);
      const percentage = metrics.totalThisMonth > 0 ? (total / metrics.totalThisMonth) * 100 : 0;
      return {
        ...cat,
        total,
        percentage,
      };
    });

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.title}>Reports & Exports</Text>
          <Text style={styles.subtitle}>
            Offline financial analytics and spreadsheet backup.
          </Text>
        </View>

        <Card style={styles.actionCard}>
          <View style={styles.cardHeader}>
            <View style={[styles.iconCircle, { backgroundColor: '#EFF5EF' }]}>
              <MaterialIcons name="table-chart" size={24} color={THEME.colors.catGroceries} />
            </View>
            <View style={styles.cardHeaderInfo}>
              <Text style={styles.cardTitle}>Spreadsheet Export (CSV)</Text>
              <Text style={styles.cardSubtitle}>
                Export all {expenses.length} records into a spreadsheet file directly on your phone. Opens in Excel, Numbers, and Google Sheets.
              </Text>
            </View>
          </View>

          <TouchableOpacity
            style={styles.csvExportBtn}
            onPress={handleExportCSV}
            disabled={isExportingCSV}
            activeOpacity={0.8}
          >
            {isExportingCSV ? (
              <ActivityIndicator size="small" color="#FFFFFF" />
            ) : (
              <>
                <MaterialIcons name="file-download" size={20} color="#FFFFFF" />
                <Text style={styles.csvExportBtnText}>Export All Records to CSV</Text>
              </>
            )}
          </TouchableOpacity>
        </Card>

        <View style={styles.metricsSummaryCard}>
          <View style={styles.metricItem}>
            <Text style={styles.metricLabel}>{monthName} Total</Text>
            <Text style={styles.metricVal}>
              {currency}{metrics.totalThisMonth.toFixed(2)}
            </Text>
          </View>
          <View style={styles.metricDivider} />
          <View style={styles.metricItem}>
            <Text style={styles.metricLabel}>{year} Total</Text>
            <Text style={styles.metricVal}>
              {currency}{metrics.totalThisYear.toFixed(2)}
            </Text>
          </View>
          <View style={styles.metricDivider} />
          <View style={styles.metricItem}>
            <Text style={styles.metricLabel}>Daily Allowance</Text>
            <Text style={[styles.metricVal, { color: THEME.colors.primary }]}>
              {currency}{metrics.safeDailyAllowance.toFixed(2)}
            </Text>
          </View>
        </View>

        <View style={styles.categorySection}>
          <Text style={styles.sectionTitle}>Category Spending Breakdown</Text>
          {categoryStats.length === 0 ? (
            <Card style={styles.emptyCard}>
              <Text style={styles.emptyText}>No expenses to categorize yet.</Text>
            </Card>
          ) : (
            categoryStats.map((item) => (
              <Card key={item.id} style={styles.categoryItemCard}>
                <View style={styles.categoryTopRow}>
                  <View style={styles.categoryTitleWrap}>
                    <View style={[styles.catIcon, { backgroundColor: item.bgColor }]}>
                      <MaterialIcons name={item.icon} size={16} color={item.color} />
                    </View>
                    <Text style={styles.categoryName}>{item.name}</Text>
                  </View>
                  <Text style={styles.categoryAmount}>
                    {currency}
                    {item.total.toFixed(2)}
                  </Text>
                </View>

                <View style={styles.categoryProgressBg}>
                  <View
                    style={[
                      styles.categoryProgressFill,
                      {
                        width: `${Math.min(100, Math.max(2, item.percentage))}%`,
                        backgroundColor: item.color,
                      },
                    ]}
                  />
                </View>

                <Text style={styles.categoryPercentage}>
                  {item.percentage.toFixed(1)}% of month's spending
                </Text>
              </Card>
            ))
          )}
        </View>

        <View style={styles.privacyCard}>
          <MaterialIcons name="verified-user" size={18} color={THEME.colors.primary} />
          <Text style={styles.privacyText}>
            100% Offline. All calculations and exports run locally on your phone. Zero cloud fees.
          </Text>
        </View>
      </ScrollView>
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
    lineHeight: 18,
  },
  actionCard: {
    marginBottom: THEME.spacing.lg,
    padding: THEME.spacing.lg,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: THEME.spacing.md,
  },
  iconCircle: {
    width: 44,
    height: 44,
    borderRadius: 22,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  cardHeaderInfo: {
    flex: 1,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 2,
  },
  cardSubtitle: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    lineHeight: 16,
  },
  csvExportBtn: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.primary,
    paddingVertical: 14,
    borderRadius: THEME.borderRadius.lg,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    ...THEME.shadows.card,
  },
  csvExportBtnText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  metricsSummaryCard: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.borderRadius.xl,
    padding: THEME.spacing.md,
    marginBottom: THEME.spacing.lg,
    ...THEME.shadows.card,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  metricItem: {
    flex: 1,
    alignItems: 'center',
  },
  metricDivider: {
    width: 1,
    backgroundColor: THEME.colors.borderSubtle,
  },
  metricLabel: {
    fontSize: 11,
    color: THEME.colors.textMuted,
    marginBottom: 4,
    fontWeight: '500',
  },
  metricVal: {
    fontSize: 14,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  categorySection: {
    marginBottom: THEME.spacing.lg,
  },
  sectionTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: THEME.spacing.md,
  },
  emptyCard: {
    alignItems: 'center',
    padding: THEME.spacing.xl,
  },
  emptyText: {
    fontSize: 13,
    color: THEME.colors.textMuted,
  },
  categoryItemCard: {
    padding: 14,
    marginBottom: 8,
  },
  categoryTopRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  categoryTitleWrap: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  catIcon: {
    width: 28,
    height: 28,
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
  },
  categoryName: {
    fontSize: 14,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
  },
  categoryAmount: {
    fontSize: 15,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  categoryProgressBg: {
    height: 6,
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: 3,
    overflow: 'hidden',
    marginBottom: 4,
  },
  categoryProgressFill: {
    height: '100%',
    borderRadius: 3,
  },
  categoryPercentage: {
    fontSize: 11,
    color: THEME.colors.textMuted,
  },
  privacyCard: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.primaryLight,
    padding: 14,
    borderRadius: THEME.borderRadius.lg,
    alignItems: 'center',
    gap: 10,
    marginTop: 4,
  },
  privacyText: {
    flex: 1,
    fontSize: 12,
    color: THEME.colors.textSecondary,
    lineHeight: 16,
  },
});
