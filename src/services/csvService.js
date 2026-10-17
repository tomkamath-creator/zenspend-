import * as FileSystem from 'expo-file-system';
import * as Sharing from 'expo-sharing';
import { getCategoryById } from '../constants/categories';

export const exportExpensesToCSV = async (expenses, currencySymbol = '$') => {
  try {
    if (!expenses || expenses.length === 0) {
      throw new Error('No expenses to export.');
    }

    const headers = ['ID', 'Date', 'Category', `Amount (${currencySymbol})`, 'Note', 'Receipt Attached', 'Created Timestamp'];
    
    const rows = expenses.map((exp) => {
      const cat = getCategoryById(exp.categoryId);
      const categoryName = cat ? cat.name : exp.categoryId;
      const safeNote = exp.note ? `"${exp.note.replace(/"/g, '""')}"` : '""';
      const hasReceipt = exp.receiptUri ? 'Yes' : 'No';
      const amount = parseFloat(exp.amount || 0).toFixed(2);
      
      return [
        exp.id,
        exp.date,
        `"${categoryName}"`,
        amount,
        safeNote,
        hasReceipt,
        exp.createdAt || exp.date,
      ].join(',');
    });

    const csvContent = [headers.join(','), ...rows].join('\r\n');
    const dateStr = new Date().toISOString().split('T')[0];
    const fileName = `SereneSpend_Expenses_${dateStr}.csv`;
    const fileUri = `${FileSystem.documentDirectory}${fileName}`;

    await FileSystem.writeAsStringAsync(fileUri, csvContent, {
      encoding: FileSystem.EncodingType.UTF8,
    });

    const isAvailable = await Sharing.isAvailableAsync();
    if (isAvailable) {
      await Sharing.shareAsync(fileUri, {
        mimeType: 'text/csv',
        dialogTitle: 'Export Expenses Spreadsheet (CSV)',
        UTI: 'public.comma-separated-values-text',
      });
    }

    return {
      success: true,
      fileUri,
      fileName,
      count: expenses.length,
    };
  } catch (error) {
    console.error('Failed to export CSV:', error);
    throw error;
  }
};
