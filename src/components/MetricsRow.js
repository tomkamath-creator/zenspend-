import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { Card } from './Card';

export const MetricsRow = ({ totalToday, totalThisMonth, totalThisYear, currency = '$' }) => {
  const metricsData = [
    {
      id: 'today',
      title: 'Today',
      amount: totalToday,
      icon: 'today',
      color: THEME.colors.primary,
      bgColor: THEME.colors.primaryLight,
    },
    {
      id: 'month',
      title: 'This Month',
      amount: totalThisMonth,
      icon: 'date-range',
      color: THEME.colors.catBills,
      bgColor: '#EDF3F8',
    },
    {
      id: 'year',
      title: 'This Year',
      amount: totalThisYear,
      icon: 'calendar-month',
      color: THEME.colors.catEntertainment,
      bgColor: '#F3F0F8',
    },
  ];

  return (
    <View style={styles.container}>
      {metricsData.map((item) => (
        <Card key={item.id} style={styles.card}>
          <View style={[styles.iconWrap, { backgroundColor: item.bgColor }]}>
            <MaterialIcons name={item.icon} size={16} color={item.color} />
          </View>
          <Text style={styles.title}>{item.title}</Text>
          <Text style={styles.amount} numberOfLines={1} adjustsFontSizeToFit>
            {currency}
            {item.amount.toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}
          </Text>
        </Card>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.lg,
    gap: THEME.spacing.sm,
  },
  card: {
    flex: 1,
    padding: THEME.spacing.md,
    alignItems: 'center',
  },
  iconWrap: {
    width: 28,
    height: 28,
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 6,
  },
  title: {
    fontSize: 11,
    color: THEME.colors.textMuted,
    fontWeight: '500',
    marginBottom: 4,
    textTransform: 'uppercase',
    letterSpacing: 0.3,
  },
  amount: {
    fontSize: 14,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
});
