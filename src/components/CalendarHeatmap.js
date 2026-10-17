import React, { useState, useMemo } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { Card } from './Card';
import { ExpenseItem } from './ExpenseItem';

const DAYS_OF_WEEK = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

export const CalendarHeatmap = ({
  expenses = [],
  currency = '$',
  onSelectExpense,
  onViewReceipt,
  onDeleteExpense,
}) => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDateStr, setSelectedDateStr] = useState(
    new Date().toISOString().split('T')[0]
  );

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const monthName = currentDate.toLocaleString('default', { month: 'long' });

  const { daysInMonthGrid, dailyTotals, maxDailySpend } = useMemo(() => {
    const totals = {};
    let max = 0;

    expenses.forEach((item) => {
      const amt = parseFloat(item.amount) || 0;
      totals[item.date] = (totals[item.date] || 0) + amt;
      if (totals[item.date] > max) max = totals[item.date];
    });

    const firstDayIndex = new Date(year, month, 1).getDay();
    const daysInCurrentMonth = new Date(year, month + 1, 0).getDate();

    const grid = [];

    for (let i = 0; i < firstDayIndex; i++) {
      grid.push({ dayNumber: null, dateStr: null, isCurrentMonth: false });
    }

    for (let d = 1; d <= daysInCurrentMonth; d++) {
      const monthStr = String(month + 1).padStart(2, '0');
      const dayStr = String(d).padStart(2, '0');
      const dateStr = `${year}-${monthStr}-${dayStr}`;

      grid.push({
        dayNumber: d,
        dateStr,
        isCurrentMonth: true,
        amount: totals[dateStr] || 0,
      });
    }

    return {
      daysInMonthGrid: grid,
      dailyTotals: totals,
      maxDailySpend: Math.max(max, 50),
    };
  }, [year, month, expenses]);

  const selectedDayExpenses = useMemo(() => {
    if (!selectedDateStr) return [];
    return expenses.filter((exp) => exp.date === selectedDateStr);
  }, [selectedDateStr, expenses]);

  const selectedDayTotal = dailyTotals[selectedDateStr] || 0;

  const prevMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1));
  };

  const nextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1));
  };

  const getHeatmapColor = (amount) => {
    if (!amount || amount === 0) return { bg: THEME.colors.surface, text: THEME.colors.textSecondary };
    const ratio = amount / maxDailySpend;
    if (ratio < 0.25) return { bg: '#E7F2E9', text: THEME.colors.primaryDark };
    if (ratio < 0.6) return { bg: '#B8D9BD', text: THEME.colors.primaryDark };
    if (ratio < 0.85) return { bg: '#7BA483', text: '#FFFFFF' };
    return { bg: '#476E4F', text: '#FFFFFF' };
  };

  const todayStr = new Date().toISOString().split('T')[0];

  return (
    <View style={styles.container}>
      <Card style={styles.calendarCard}>
        <View style={styles.monthHeader}>
          <TouchableOpacity onPress={prevMonth} style={styles.navButton}>
            <MaterialIcons name="chevron-left" size={24} color={THEME.colors.textPrimary} />
          </TouchableOpacity>

          <View style={styles.monthTitleWrap}>
            <Text style={styles.monthTitle}>
              {monthName} {year}
            </Text>
            <Text style={styles.monthSub}>Daily Spending Heatmap</Text>
          </View>

          <TouchableOpacity onPress={nextMonth} style={styles.navButton}>
            <MaterialIcons name="chevron-right" size={24} color={THEME.colors.textPrimary} />
          </TouchableOpacity>
        </View>

        <View style={styles.weekRow}>
          {DAYS_OF_WEEK.map((day) => (
            <Text key={day} style={styles.weekDayText}>
              {day}
            </Text>
          ))}
        </View>

        <View style={styles.gridContainer}>
          {daysInMonthGrid.map((cell, index) => {
            if (!cell.isCurrentMonth) {
              return <View key={`empty-${index}`} style={styles.emptyCell} />;
            }

            const { bg, text } = getHeatmapColor(cell.amount);
            const isSelected = selectedDateStr === cell.dateStr;
            const isToday = cell.dateStr === todayStr;

            return (
              <TouchableOpacity
                key={cell.dateStr}
                style={[
                  styles.dayCell,
                  { backgroundColor: bg },
                  isSelected && styles.dayCellSelected,
                  isToday && styles.dayCellToday,
                ]}
                onPress={() => setSelectedDateStr(cell.dateStr)}
                activeOpacity={0.7}
              >
                <Text style={[styles.dayText, { color: text }, isSelected && styles.dayTextSelected]}>
                  {cell.dayNumber}
                </Text>
                {cell.amount > 0 && (
                  <View
                    style={[
                      styles.spendDot,
                      { backgroundColor: text === '#FFFFFF' ? '#FFFFFF' : THEME.colors.primary },
                    ]}
                  />
                )}
              </TouchableOpacity>
            );
          })}
        </View>

        <View style={styles.legendRow}>
          <Text style={styles.legendLabel}>Intensity:</Text>
          <View style={styles.legendItem}>
            <View style={[styles.legendBox, { backgroundColor: THEME.colors.surface, borderWidth: 1, borderColor: THEME.colors.border }]} />
            <Text style={styles.legendText}>$0</Text>
          </View>
          <View style={styles.legendItem}>
            <View style={[styles.legendBox, { backgroundColor: '#E7F2E9' }]} />
            <Text style={styles.legendText}>Low</Text>
          </View>
          <View style={styles.legendItem}>
            <View style={[styles.legendBox, { backgroundColor: '#B8D9BD' }]} />
            <Text style={styles.legendText}>Med</Text>
          </View>
          <View style={styles.legendItem}>
            <View style={[styles.legendBox, { backgroundColor: '#476E4F' }]} />
            <Text style={styles.legendText}>High</Text>
          </View>
        </View>
      </Card>

      <View style={styles.selectedDaySection}>
        <View style={styles.selectedDayHeader}>
          <View>
            <Text style={styles.selectedDayTitle}>
              {new Date(selectedDateStr + 'T00:00:00').toLocaleDateString('en-US', {
                weekday: 'short',
                month: 'short',
                day: 'numeric',
              })}
            </Text>
            <Text style={styles.selectedDayCount}>
              {selectedDayExpenses.length} transaction
              {selectedDayExpenses.length === 1 ? '' : 's'}
            </Text>
          </View>

          <Text style={styles.selectedDayTotal}>
            {currency}
            {selectedDayTotal.toFixed(2)}
          </Text>
        </View>

        {selectedDayExpenses.length === 0 ? (
          <View style={styles.emptyDayBox}>
            <MaterialIcons name="eco" size={28} color={THEME.colors.sageMuted} />
            <Text style={styles.emptyDayText}>No money spent on this peaceful day.</Text>
          </View>
        ) : (
          selectedDayExpenses.map((expense) => (
            <ExpenseItem
              key={expense.id}
              expense={expense}
              currency={currency}
              onPress={onSelectExpense}
              onViewReceipt={onViewReceipt}
              onDelete={onDeleteExpense}
            />
          ))
        )}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    marginBottom: THEME.spacing.xl,
  },
  calendarCard: {
    padding: THEME.spacing.md,
    marginBottom: THEME.spacing.lg,
  },
  monthHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: THEME.spacing.md,
  },
  navButton: {
    padding: 6,
    borderRadius: THEME.borderRadius.round,
    backgroundColor: THEME.colors.surfaceSecondary,
  },
  monthTitleWrap: {
    alignItems: 'center',
  },
  monthTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  monthSub: {
    fontSize: 11,
    color: THEME.colors.textMuted,
    marginTop: 2,
  },
  weekRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginBottom: 8,
    paddingBottom: 6,
    borderBottomWidth: 1,
    borderBottomColor: THEME.colors.borderSubtle,
  },
  weekDayText: {
    width: 38,
    textAlign: 'center',
    fontSize: 11,
    fontWeight: '600',
    color: THEME.colors.textMuted,
    textTransform: 'uppercase',
  },
  gridContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'flex-start',
  },
  emptyCell: {
    width: '14.28%',
    height: 42,
  },
  dayCell: {
    width: '14.28%',
    height: 42,
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 8,
    marginVertical: 2,
    position: 'relative',
  },
  dayCellSelected: {
    borderWidth: 2,
    borderColor: THEME.colors.primaryDark,
  },
  dayCellToday: {
    borderWidth: 1.5,
    borderColor: THEME.colors.primary,
  },
  dayText: {
    fontSize: 13,
    fontWeight: '600',
  },
  dayTextSelected: {
    fontWeight: '800',
  },
  spendDot: {
    width: 4,
    height: 4,
    borderRadius: 2,
    position: 'absolute',
    bottom: 3,
  },
  legendRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: THEME.spacing.md,
    paddingTop: THEME.spacing.sm,
    borderTopWidth: 1,
    borderTopColor: THEME.colors.borderSubtle,
    gap: 8,
  },
  legendLabel: {
    fontSize: 11,
    color: THEME.colors.textMuted,
  },
  legendItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  legendBox: {
    width: 12,
    height: 12,
    borderRadius: 3,
  },
  legendText: {
    fontSize: 10,
    color: THEME.colors.textSecondary,
  },
  selectedDaySection: {
    marginTop: 4,
  },
  selectedDayHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: THEME.spacing.md,
    paddingHorizontal: 4,
  },
  selectedDayTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  selectedDayCount: {
    fontSize: 12,
    color: THEME.colors.textMuted,
    marginTop: 1,
  },
  selectedDayTotal: {
    fontSize: 20,
    fontWeight: '700',
    color: THEME.colors.primaryDark,
  },
  emptyDayBox: {
    backgroundColor: THEME.colors.surface,
    padding: THEME.spacing.xl,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
  },
  emptyDayText: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
  },
});
