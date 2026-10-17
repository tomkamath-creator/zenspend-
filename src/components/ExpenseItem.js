import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Image } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { getCategoryById } from '../constants/categories';

export const ExpenseItem = ({
  expense,
  currency = '$',
  onPress,
  onViewReceipt,
  onDelete,
}) => {
  const category = getCategoryById(expense.categoryId);

  return (
    <TouchableOpacity
      style={styles.container}
      onPress={() => onPress && onPress(expense)}
      activeOpacity={0.7}
    >
      <View style={[styles.iconContainer, { backgroundColor: category.bgColor }]}>
        <MaterialIcons
          name={category.icon}
          size={20}
          color={category.color}
        />
      </View>

      <View style={styles.contentContainer}>
        <Text style={styles.title} numberOfLines={1}>
          {expense.note || category.name}
        </Text>
        <View style={styles.metaRow}>
          <Text style={styles.categoryName}>{category.name}</Text>
          <Text style={styles.metaDot}>•</Text>
          <Text style={styles.dateText}>{expense.date}</Text>
        </View>
      </View>

      <View style={styles.rightContainer}>
        {expense.receiptUri ? (
          <TouchableOpacity
            style={styles.receiptThumbnailContainer}
            onPress={() => onViewReceipt && onViewReceipt(expense.receiptUri, expense)}
            activeOpacity={0.8}
          >
            <Image
              source={{ uri: expense.receiptUri }}
              style={styles.receiptThumbnail}
            />
            <View style={styles.receiptBadge}>
              <MaterialIcons name="receipt" size={10} color="#FFFFFF" />
            </View>
          </TouchableOpacity>
        ) : null}

        <Text style={styles.amountText}>
          {currency}
          {parseFloat(expense.amount || 0).toFixed(2)}
        </Text>

        {onDelete && (
          <TouchableOpacity
            style={styles.deleteButton}
            onPress={() => onDelete(expense.id)}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          >
            <MaterialIcons name="delete-outline" size={18} color={THEME.colors.textMuted} />
          </TouchableOpacity>
        )}
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.surface,
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderRadius: THEME.borderRadius.lg,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: THEME.colors.borderSubtle,
    ...THEME.shadows.card,
  },
  iconContainer: {
    width: 44,
    height: 44,
    borderRadius: 22,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  contentContainer: {
    flex: 1,
    marginRight: 10,
  },
  title: {
    fontSize: 15,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginBottom: 3,
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  categoryName: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    fontWeight: '500',
  },
  metaDot: {
    fontSize: 12,
    color: THEME.colors.textMuted,
    marginHorizontal: 5,
  },
  dateText: {
    fontSize: 12,
    color: THEME.colors.textMuted,
  },
  rightContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  receiptThumbnailContainer: {
    position: 'relative',
    marginRight: 8,
  },
  receiptThumbnail: {
    width: 32,
    height: 32,
    borderRadius: 6,
    backgroundColor: THEME.colors.surfaceSecondary,
  },
  receiptBadge: {
    position: 'absolute',
    bottom: -2,
    right: -2,
    backgroundColor: THEME.colors.primary,
    borderRadius: 6,
    width: 14,
    height: 14,
    justifyContent: 'center',
    alignItems: 'center',
  },
  amountText: {
    fontSize: 16,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    letterSpacing: -0.3,
  },
  deleteButton: {
    marginLeft: 10,
    padding: 2,
  },
});
