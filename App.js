import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
  ActivityIndicator,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from './src/constants/theme';
import { ExpenseProvider, useExpense } from './src/context/ExpenseContext';
import { DashboardScreen } from './src/screens/DashboardScreen';
import { ExpensesScreen } from './src/screens/ExpensesScreen';
import { CalendarScreen } from './src/screens/CalendarScreen';
import { ReportsScreen } from './src/screens/ReportsScreen';
import { SettingsScreen } from './src/screens/SettingsScreen';
import { AddExpenseModal } from './src/components/AddExpenseModal';
import { ReceiptViewerModal } from './src/components/ReceiptViewerModal';

const MainNavigator = () => {
  const {
    isLoading,
    currency,
    addExpense,
    updateExpense,
  } = useExpense();

  const [activeTab, setActiveTab] = useState('home');
  const [isAddModalVisible, setIsAddModalVisible] = useState(false);
  const [editingExpense, setEditingExpense] = useState(null);
  const [viewingReceipt, setViewingReceipt] = useState({ visible: false, uri: null, expense: null });

  if (isLoading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={THEME.colors.primary} />
        <Text style={styles.loadingText}>Restoring offline records...</Text>
      </View>
    );
  }

  const handleOpenAdd = () => {
    setEditingExpense(null);
    setIsAddModalVisible(true);
  };

  const handleOpenEdit = (expense) => {
    setEditingExpense(expense);
    setIsAddModalVisible(true);
  };

  const handleSaveExpense = async (data) => {
    if (data.id) {
      await updateExpense(data.id, data);
    } else {
      await addExpense(data);
    }
  };

  const handleViewReceipt = (uri, expense) => {
    setViewingReceipt({ visible: true, uri, expense });
  };

  const tabs = [
    { id: 'home', label: 'Home', icon: 'spa' },
    { id: 'expenses', label: 'Expenses', icon: 'format-list-bulleted' },
    { id: 'calendar', label: 'Calendar', icon: 'calendar-month' },
    { id: 'reports', label: 'Reports', icon: 'insights' },
    { id: 'settings', label: 'Settings', icon: 'settings' },
  ];

  return (
    <View style={styles.rootContainer}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <View style={styles.screenContainer}>
        {activeTab === 'home' && (
          <DashboardScreen
            onOpenAddExpense={handleOpenAdd}
            onOpenEditExpense={handleOpenEdit}
            onViewReceipt={handleViewReceipt}
            onNavigateToExpenses={() => setActiveTab('expenses')}
          />
        )}
        {activeTab === 'expenses' && (
          <ExpensesScreen
            onOpenAddExpense={handleOpenAdd}
            onOpenEditExpense={handleOpenEdit}
            onViewReceipt={handleViewReceipt}
          />
        )}
        {activeTab === 'calendar' && (
          <CalendarScreen
            onSelectExpense={handleOpenEdit}
            onViewReceipt={handleViewReceipt}
          />
        )}
        {activeTab === 'reports' && <ReportsScreen />}
        {activeTab === 'settings' && <SettingsScreen />}
      </View>

      <SafeAreaView style={styles.bottomNavSafe}>
        <View style={styles.bottomNav}>
          {tabs.map((tab) => {
            const isActive = activeTab === tab.id;
            return (
              <TouchableOpacity
                key={tab.id}
                style={[styles.navItem, isActive && styles.navItemActive]}
                onPress={() => setActiveTab(tab.id)}
                activeOpacity={0.7}
              >
                <View style={[styles.navIconContainer, isActive && styles.navIconContainerActive]}>
                  <MaterialIcons
                    name={tab.icon}
                    size={22}
                    color={isActive ? THEME.colors.primary : THEME.colors.textMuted}
                  />
                </View>
                <Text
                  style={[
                    styles.navLabel,
                    isActive && styles.navLabelActive,
                  ]}
                >
                  {tab.label}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>
      </SafeAreaView>

      <AddExpenseModal
        visible={isAddModalVisible}
        initialExpense={editingExpense}
        currency={currency}
        onClose={() => {
          setIsAddModalVisible(false);
          setEditingExpense(null);
        }}
        onSave={handleSaveExpense}
      />

      <ReceiptViewerModal
        visible={viewingReceipt.visible}
        receiptUri={viewingReceipt.uri}
        expense={viewingReceipt.expense}
        onClose={() => setViewingReceipt({ visible: false, uri: null, expense: null })}
      />
    </View>
  );
};

export default function App() {
  return (
    <ExpenseProvider>
      <MainNavigator />
    </ExpenseProvider>
  );
}

const styles = StyleSheet.create({
  rootContainer: {
    flex: 1,
    backgroundColor: THEME.colors.background,
  },
  screenContainer: {
    flex: 1,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: THEME.colors.background,
    gap: 12,
  },
  loadingText: {
    fontSize: 14,
    color: THEME.colors.textSecondary,
    fontWeight: '500',
  },
  bottomNavSafe: {
    backgroundColor: THEME.colors.surface,
    borderTopWidth: 1,
    borderTopColor: THEME.colors.border,
  },
  bottomNav: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingVertical: 8,
    paddingHorizontal: 8,
  },
  navItem: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 4,
    paddingHorizontal: 10,
    borderRadius: THEME.borderRadius.lg,
  },
  navItemActive: {},
  navIconContainer: {
    padding: 4,
    borderRadius: THEME.borderRadius.round,
    marginBottom: 2,
  },
  navIconContainerActive: {
    backgroundColor: THEME.colors.primaryLight,
  },
  navLabel: {
    fontSize: 11,
    fontWeight: '500',
    color: THEME.colors.textMuted,
  },
  navLabelActive: {
    color: THEME.colors.primary,
    fontWeight: '700',
  },
});
