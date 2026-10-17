import AsyncStorage from '@react-native-async-storage/async-storage';
import * as FileSystem from 'expo-file-system';

const STORAGE_KEYS = {
  EXPENSES: '@serenespend_expenses_v1',
  BUDGET: '@serenespend_monthly_budget_v1',
  CURRENCY: '@serenespend_currency_v1',
  INITIALIZED: '@serenespend_initialized_v1',
};

const RECEIPTS_DIR = `${FileSystem.documentDirectory}receipts/`;

const ensureReceiptsDirectory = async () => {
  try {
    const dirInfo = await FileSystem.getInfoAsync(RECEIPTS_DIR);
    if (!dirInfo.exists) {
      await FileSystem.makeDirectoryAsync(RECEIPTS_DIR, { intermediates: true });
    }
  } catch (error) {
    console.warn('Could not initialize receipts directory:', error);
  }
};

export const saveReceiptImageOffline = async (sourceUri) => {
  try {
    await ensureReceiptsDirectory();
    const filename = `receipt_${Date.now()}_${Math.random().toString(36).substring(7)}.jpg`;
    const destinationUri = `${RECEIPTS_DIR}${filename}`;
    
    await FileSystem.copyAsync({
      from: sourceUri,
      to: destinationUri,
    });
    
    return destinationUri;
  } catch (error) {
    console.warn('Failed to save receipt image offline, falling back to source URI:', error);
    return sourceUri;
  }
};

export const deleteReceiptImage = async (uri) => {
  if (!uri || !uri.startsWith(RECEIPTS_DIR)) return;
  try {
    const fileInfo = await FileSystem.getInfoAsync(uri);
    if (fileInfo.exists) {
      await FileSystem.deleteAsync(uri, { idempotent: true });
    }
  } catch (error) {
    console.warn('Failed to delete receipt file:', error);
  }
};

const getSampleExpenses = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  const day = today.getDate();

  const formatDate = (d, mOffset = 0, dOffset = 0) => {
    const date = new Date(year, month + mOffset, d + dOffset);
    return date.toISOString().split('T')[0];
  };

  return [
    {
      id: 'sample_1',
      amount: 48.50,
      categoryId: 'groceries',
      note: 'Organic fresh produce & herbal tea',
      date: formatDate(day),
      receiptUri: null,
      createdAt: new Date().toISOString(),
    },
    {
      id: 'sample_2',
      amount: 14.25,
      categoryId: 'food',
      note: 'Matcha latte & avocado toast',
      date: formatDate(day),
      receiptUri: null,
      createdAt: new Date(Date.now() - 3600000).toISOString(),
    },
    {
      id: 'sample_3',
      amount: 32.00,
      categoryId: 'travel',
      note: 'Train pass for commute',
      date: formatDate(day, 0, -1),
      receiptUri: null,
      createdAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      id: 'sample_4',
      amount: 85.00,
      categoryId: 'bills',
      note: 'Fiber home internet bill',
      date: formatDate(day, 0, -3),
      receiptUri: null,
      createdAt: new Date(Date.now() - 86400000 * 3).toISOString(),
    },
    {
      id: 'sample_5',
      amount: 25.50,
      categoryId: 'health',
      note: 'Aromatherapy lavender essential oils',
      date: formatDate(day, 0, -5),
      receiptUri: null,
      createdAt: new Date(Date.now() - 86400000 * 5).toISOString(),
    },
    {
      id: 'sample_6',
      amount: 62.80,
      categoryId: 'groceries',
      note: 'Weekly essentials & whole grains',
      date: formatDate(day, 0, -8),
      receiptUri: null,
      createdAt: new Date(Date.now() - 86400000 * 8).toISOString(),
    },
    {
      id: 'sample_7',
      amount: 19.99,
      categoryId: 'entertainment',
      note: 'Nature documentary streaming pass',
      date: formatDate(day, 0, -12),
      receiptUri: null,
      createdAt: new Date(Date.now() - 86400000 * 12).toISOString(),
    },
  ];
};

export const loadExpenses = async () => {
  try {
    const isInitialized = await AsyncStorage.getItem(STORAGE_KEYS.INITIALIZED);
    if (!isInitialized) {
      const sample = getSampleExpenses();
      await AsyncStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(sample));
      await AsyncStorage.setItem(STORAGE_KEYS.BUDGET, '1500');
      await AsyncStorage.setItem(STORAGE_KEYS.INITIALIZED, 'true');
      return sample;
    }

    const data = await AsyncStorage.getItem(STORAGE_KEYS.EXPENSES);
    return data ? JSON.parse(data) : [];
  } catch (error) {
    console.error('Failed to load expenses from offline storage:', error);
    return [];
  }
};

export const saveExpenses = async (expenses) => {
  try {
    await AsyncStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(expenses));
  } catch (error) {
    console.error('Failed to save expenses to offline storage:', error);
  }
};

export const loadBudget = async () => {
  try {
    const data = await AsyncStorage.getItem(STORAGE_KEYS.BUDGET);
    return data ? parseFloat(data) : 1500;
  } catch (error) {
    return 1500;
  }
};

export const saveBudget = async (budget) => {
  try {
    await AsyncStorage.setItem(STORAGE_KEYS.BUDGET, budget.toString());
  } catch (error) {
    console.error('Failed to save budget:', error);
  }
};

export const loadCurrency = async () => {
  try {
    const data = await AsyncStorage.getItem(STORAGE_KEYS.CURRENCY);
    return data || '$';
  } catch (error) {
    return '$';
  }
};

export const saveCurrency = async (currency) => {
  try {
    await AsyncStorage.setItem(STORAGE_KEYS.CURRENCY, currency);
  } catch (error) {
    console.error('Failed to save currency:', error);
  }
};


export const clearAllData = async () => {
  try {
    await AsyncStorage.removeItem(STORAGE_KEYS.EXPENSES);
    await AsyncStorage.removeItem(STORAGE_KEYS.BUDGET);
    await AsyncStorage.removeItem(STORAGE_KEYS.INITIALIZED);
  } catch (error) {
    console.error('Failed to clear data:', error);
  }
};
