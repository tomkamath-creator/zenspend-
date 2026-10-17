import React, { createContext, useContext, useState, useEffect, useMemo } from 'react';
import {
  loadExpenses,
  saveExpenses,
  loadBudget,
  saveBudget,
  loadCurrency,
  saveCurrency,
  loadLockSettings,
  saveLockSettings,
  saveReceiptImageOffline,
  deleteReceiptImage,
  clearAllData,
} from '../services/storage';

const ExpenseContext = createContext(null);

export const ExpenseProvider = ({ children }) => {
  const [expenses, setExpenses] = useState([]);
  const [budget, setBudget] = useState(1500);
  const [currency, setCurrency] = useState('$');
  const [lockSettings, setLockSettings] = useState({ isEnabled: false, pin: null, useBiometrics: true });
  const [isLocked, setIsLocked] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const init = async () => {
      try {
        const [savedExpenses, savedBudget, savedCurrency, savedLock] = await Promise.all([
          loadExpenses(),
          loadBudget(),
          loadCurrency(),
          loadLockSettings(),
        ]);

        setExpenses(savedExpenses);
        setBudget(savedBudget);
        setCurrency(savedCurrency);
        setLockSettings(savedLock);

        if (savedLock.isEnabled) {
          setIsLocked(true);
        }
      } catch (err) {
        console.error('Initialization error:', err);
      } finally {
        setIsLoading(false);
      }
    };

    init();
  }, []);

  const metrics = useMemo(() => {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth();
    const todayStr = now.toISOString().split('T')[0];

    const totalDaysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
    const currentDay = now.getDate();
    const remainingDays = Math.max(1, totalDaysInMonth - currentDay + 1);

    let totalToday = 0;
    let totalThisMonth = 0;
    let totalThisYear = 0;
    const dailySpendingMap = {};
    const categoryTotals = {};

    expenses.forEach((item) => {
      const amount = parseFloat(item.amount) || 0;
      const itemDate = new Date(item.date);
      const itemYear = itemDate.getFullYear();
      const itemMonth = itemDate.getMonth();

      dailySpendingMap[item.date] = (dailySpendingMap[item.date] || 0) + amount;
      categoryTotals[item.categoryId] = (categoryTotals[item.categoryId] || 0) + amount;

      if (item.date === todayStr) {
        totalToday += amount;
      }
      if (itemYear === currentYear && itemMonth === currentMonth) {
        totalThisMonth += amount;
      }
      if (itemYear === currentYear) {
        totalThisYear += amount;
      }
    });

    const remainingBudget = budget - totalThisMonth;
    const safeDailyAllowance = remainingBudget > 0 ? remainingBudget / remainingDays : 0;
    const spentPercentage = budget > 0 ? (totalThisMonth / budget) * 100 : 0;

    return {
      totalToday,
      totalThisMonth,
      totalThisYear,
      remainingBudget,
      safeDailyAllowance,
      spentPercentage,
      remainingDays,
      totalDaysInMonth,
      currentDay,
      dailySpendingMap,
      categoryTotals,
    };
  }, [expenses, budget]);

  const addExpense = async ({ amount, categoryId, note, date, receiptUri }) => {
    let finalReceiptUri = null;
    if (receiptUri) {
      finalReceiptUri = await saveReceiptImageOffline(receiptUri);
    }

    const newExpense = {
      id: `exp_${Date.now()}_${Math.random().toString(36).substring(6)}`,
      amount: parseFloat(amount),
      categoryId,
      note: note ? note.trim() : '',
      date: date || new Date().toISOString().split('T')[0],
      receiptUri: finalReceiptUri,
      createdAt: new Date().toISOString(),
    };

    const updated = [newExpense, ...expenses];
    setExpenses(updated);
    await saveExpenses(updated);
    return newExpense;
  };

  const updateExpense = async (id, updatedFields) => {
    let finalReceiptUri = updatedFields.receiptUri;
    if (updatedFields.receiptUri && !updatedFields.receiptUri.includes('receipts/')) {
      finalReceiptUri = await saveReceiptImageOffline(updatedFields.receiptUri);
    }

    const updated = expenses.map((item) => {
      if (item.id === id) {
        return {
          ...item,
          ...updatedFields,
          amount: parseFloat(updatedFields.amount !== undefined ? updatedFields.amount : item.amount),
          receiptUri: finalReceiptUri !== undefined ? finalReceiptUri : item.receiptUri,
        };
      }
      return item;
    });

    setExpenses(updated);
    await saveExpenses(updated);
  };

  const deleteExpense = async (id) => {
    const target = expenses.find((e) => e.id === id);
    if (target && target.receiptUri) {
      await deleteReceiptImage(target.receiptUri);
    }

    const updated = expenses.filter((e) => e.id !== id);
    setExpenses(updated);
    await saveExpenses(updated);
  };

  const updateBudget = async (newBudget) => {
    const val = parseFloat(newBudget);
    if (!isNaN(val) && val >= 0) {
      setBudget(val);
      await saveBudget(val);
    }
  };

  const updateCurrency = async (newCurrency) => {
    setCurrency(newCurrency);
    await saveCurrency(newCurrency);
  };

  const updateLockSettings = async (newSettings) => {
    setLockSettings(newSettings);
    await saveLockSettings(newSettings);
  };

  const unlockApp = () => {
    setIsLocked(false);
  };

  const lockApp = () => {
    if (lockSettings.isEnabled) {
      setIsLocked(true);
    }
  };

  const resetAllExpenses = async () => {
    for (const exp of expenses) {
      if (exp.receiptUri) {
        await deleteReceiptImage(exp.receiptUri);
      }
    }
    await clearAllData();
    const fresh = await loadExpenses();
    setExpenses(fresh);
    const freshBudget = await loadBudget();
    setBudget(freshBudget);
  };

  return (
    <ExpenseContext.Provider
      value={{
        expenses,
        budget,
        currency,
        lockSettings,
        isLocked,
        isLoading,
        metrics,
        addExpense,
        updateExpense,
        deleteExpense,
        updateBudget,
        updateCurrency,
        updateLockSettings,
        unlockApp,
        lockApp,
        resetAllExpenses,
      }}
    >
      {children}
    </ExpenseContext.Provider>
  );
};

export const useExpense = () => {
  const context = useContext(ExpenseContext);
  if (!context) {
    throw new Error('useExpense must be used within an ExpenseProvider');
  }
  return context;
};
