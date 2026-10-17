import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { THEME } from '../constants/theme';
import { useExpense } from '../context/ExpenseContext';
import { CalendarHeatmap } from '../components/CalendarHeatmap';

export const CalendarScreen = ({
  onSelectExpense,
  onViewReceipt,
}) => {
  const { expenses, currency, deleteExpense } = useExpense();

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.title}>Calendar Heatmap</Text>
          <Text style={styles.subtitle}>
            Gentle sage green shades show days with higher spending.
          </Text>
        </View>

        <CalendarHeatmap
          expenses={expenses}
          currency={currency}
          onSelectExpense={onSelectExpense}
          onViewReceipt={onViewReceipt}
          onDeleteExpense={deleteExpense}
        />
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
    marginBottom: THEME.spacing.md,
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
});
