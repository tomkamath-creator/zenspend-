import React, { useState, useMemo } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  FlatList,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { CATEGORIES, getCategoryById } from '../constants/categories';
import { useExpense } from '../context/ExpenseContext';
import { ExpenseItem } from '../components/ExpenseItem';

export const ExpensesScreen = ({
  onOpenAddExpense,
  onOpenEditExpense,
  onViewReceipt,
}) => {
  const { expenses, currency, deleteExpense } = useExpense();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [dateFilter, setDateFilter] = useState('all');
  const [sortOrder, setSortOrder] = useState('date_desc');

  const filteredExpenses = useMemo(() => {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth();

    return expenses
      .filter((item) => {
        if (searchQuery.trim()) {
          const q = searchQuery.toLowerCase();
          const category = getCategoryById(item.categoryId);
          const matchesNote = item.note && item.note.toLowerCase().includes(q);
          const matchesCat = category && category.name.toLowerCase().includes(q);
          const matchesAmt = item.amount.toString().includes(q);
          if (!matchesNote && !matchesCat && !matchesAmt) return false;
        }

        if (selectedCategory !== 'all' && item.categoryId !== selectedCategory) {
          return false;
        }

        if (dateFilter === 'month') {
          const d = new Date(item.date);
          if (d.getFullYear() !== currentYear || d.getMonth() !== currentMonth) {
            return false;
          }
        } else if (dateFilter === 'year') {
          const d = new Date(item.date);
          if (d.getFullYear() !== currentYear) {
            return false;
          }
        }

        return true;
      })
      .sort((a, b) => {
        if (sortOrder === 'date_desc') {
          return new Date(b.date) - new Date(a.date);
        } else if (sortOrder === 'date_asc') {
          return new Date(a.date) - new Date(b.date);
        } else if (sortOrder === 'amount_desc') {
          return b.amount - a.amount;
        } else if (sortOrder === 'amount_asc') {
          return a.amount - b.amount;
        }
        return 0;
      });
  }, [expenses, searchQuery, selectedCategory, dateFilter, sortOrder]);

  const filteredTotal = useMemo(() => {
    return filteredExpenses.reduce((sum, item) => sum + (parseFloat(item.amount) || 0), 0);
  }, [filteredExpenses]);

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>All Expenses</Text>
          <TouchableOpacity
            style={styles.addBtn}
            onPress={onOpenAddExpense}
            activeOpacity={0.8}
          >
            <MaterialIcons name="add" size={20} color="#FFFFFF" />
            <Text style={styles.addBtnText}>Add</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.searchBar}>
          <MaterialIcons name="search" size={20} color={THEME.colors.textMuted} />
          <TextInput
            style={styles.searchInput}
            placeholder="Search notes, categories, or amounts..."
            placeholderTextColor={THEME.colors.textMuted}
            value={searchQuery}
            onChangeText={setSearchQuery}
            clearButtonMode="while-editing"
          />
          {searchQuery ? (
            <TouchableOpacity onPress={() => setSearchQuery('')}>
              <MaterialIcons name="close" size={18} color={THEME.colors.textSecondary} />
            </TouchableOpacity>
          ) : null}
        </View>

        <View style={styles.categoryFiltersContainer}>
          <FlatList
            horizontal
            showsHorizontalScrollIndicator={false}
            data={[{ id: 'all', name: 'All' }, ...CATEGORIES]}
            keyExtractor={(item) => item.id}
            contentContainerStyle={styles.categoryChipsList}
            renderItem={({ item }) => {
              const isSelected = selectedCategory === item.id;
              return (
                <TouchableOpacity
                  style={[
                    styles.catFilterChip,
                    isSelected && styles.catFilterChipActive,
                  ]}
                  onPress={() => setSelectedCategory(item.id)}
                  activeOpacity={0.7}
                >
                  <Text
                    style={[
                      styles.catFilterChipText,
                      isSelected && styles.catFilterChipTextActive,
                    ]}
                  >
                    {item.name}
                  </Text>
                </TouchableOpacity>
              );
            }}
          />
        </View>

        <View style={styles.filterMetaRow}>
          <View style={styles.dateFilterGroup}>
            {[
              { id: 'all', label: 'All' },
              { id: 'month', label: 'This Month' },
              { id: 'year', label: 'This Year' },
            ].map((d) => (
              <TouchableOpacity
                key={d.id}
                style={[
                  styles.dateFilterBtn,
                  dateFilter === d.id && styles.dateFilterBtnActive,
                ]}
                onPress={() => setDateFilter(d.id)}
              >
                <Text
                  style={[
                    styles.dateFilterText,
                    dateFilter === d.id && styles.dateFilterTextActive,
                  ]}
                >
                  {d.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          <TouchableOpacity
            style={styles.sortToggleBtn}
            onPress={() => {
              const orderMap = {
                date_desc: 'amount_desc',
                amount_desc: 'date_asc',
                date_asc: 'amount_asc',
                amount_asc: 'date_desc',
              };
              setSortOrder(orderMap[sortOrder] || 'date_desc');
            }}
          >
            <MaterialIcons name="sort" size={14} color={THEME.colors.textSecondary} />
            <Text style={styles.sortToggleText}>
              {sortOrder.startsWith('amount') ? 'Amount' : 'Date'}{' '}
              {sortOrder.endsWith('desc') ? '↓' : '↑'}
            </Text>
          </TouchableOpacity>
        </View>

        <View style={styles.resultsSummaryRow}>
          <Text style={styles.resultsCountText}>
            {filteredExpenses.length} record{filteredExpenses.length === 1 ? '' : 's'}
          </Text>
          <Text style={styles.resultsTotalText}>
            Total: {currency}
            {filteredTotal.toFixed(2)}
          </Text>
        </View>

        <FlatList
          data={filteredExpenses}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.listContent}
          showsVerticalScrollIndicator={false}
          renderItem={({ item }) => (
            <ExpenseItem
              expense={item}
              currency={currency}
              onPress={onOpenEditExpense}
              onViewReceipt={onViewReceipt}
              onDelete={deleteExpense}
            />
          )}
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <MaterialIcons name="search-off" size={40} color={THEME.colors.sageMuted} />
              <Text style={styles.emptyTitle}>No matching expenses found</Text>
              <Text style={styles.emptySub}>
                Try adjusting your search query or category filters.
              </Text>
            </View>
          }
        />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: THEME.colors.background,
  },
  container: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: THEME.spacing.lg,
    paddingTop: THEME.spacing.sm,
    paddingBottom: THEME.spacing.sm,
  },
  headerTitle: {
    fontSize: 22,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  addBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.primary,
    paddingHorizontal: 12,
    paddingVertical: 7,
    borderRadius: THEME.borderRadius.round,
    gap: 4,
  },
  addBtnText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  searchBar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.surface,
    marginHorizontal: THEME.spacing.lg,
    marginTop: THEME.spacing.xs,
    marginBottom: THEME.spacing.sm,
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: THEME.borderRadius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    gap: 8,
  },
  searchInput: {
    flex: 1,
    fontSize: 14,
    color: THEME.colors.textPrimary,
    padding: 0,
  },
  categoryFiltersContainer: {
    marginBottom: THEME.spacing.xs,
  },
  categoryChipsList: {
    paddingHorizontal: THEME.spacing.lg,
    gap: 6,
    paddingVertical: 4,
  },
  catFilterChip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: THEME.borderRadius.round,
    backgroundColor: THEME.colors.surface,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  catFilterChipActive: {
    backgroundColor: THEME.colors.primaryLight,
    borderColor: THEME.colors.primary,
  },
  catFilterChipText: {
    fontSize: 12,
    fontWeight: '500',
    color: THEME.colors.textSecondary,
  },
  catFilterChipTextActive: {
    color: THEME.colors.primary,
    fontWeight: '700',
  },
  filterMetaRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: THEME.spacing.lg,
    marginVertical: 4,
  },
  dateFilterGroup: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.surfaceSecondary,
    borderRadius: THEME.borderRadius.round,
    padding: 2,
  },
  dateFilterBtn: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: THEME.borderRadius.round,
  },
  dateFilterBtnActive: {
    backgroundColor: THEME.colors.surface,
    ...THEME.shadows.card,
  },
  dateFilterText: {
    fontSize: 11,
    fontWeight: '500',
    color: THEME.colors.textSecondary,
  },
  dateFilterTextActive: {
    color: THEME.colors.textPrimary,
    fontWeight: '600',
  },
  sortToggleBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingHorizontal: 8,
    paddingVertical: 4,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.borderRadius.round,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  sortToggleText: {
    fontSize: 11,
    color: THEME.colors.textSecondary,
    fontWeight: '500',
  },
  resultsSummaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: 6,
  },
  resultsCountText: {
    fontSize: 12,
    color: THEME.colors.textMuted,
  },
  resultsTotalText: {
    fontSize: 13,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
  },
  listContent: {
    paddingHorizontal: THEME.spacing.lg,
    paddingBottom: 90,
  },
  emptyContainer: {
    backgroundColor: THEME.colors.surface,
    padding: THEME.spacing.xxl,
    borderRadius: THEME.borderRadius.xl,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    alignItems: 'center',
    marginTop: THEME.spacing.lg,
  },
  emptyTitle: {
    fontSize: 15,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginTop: 8,
    marginBottom: 4,
  },
  emptySub: {
    fontSize: 12,
    color: THEME.colors.textSecondary,
    textAlign: 'center',
  },
});
