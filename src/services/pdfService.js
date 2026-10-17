import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import { getCategoryById } from '../constants/categories';

const generateReportHTML = ({
  expenses,
  monthlyBudget,
  currencySymbol = '$',
  monthName,
  year,
  metrics,
}) => {
  const generatedAt = new Date().toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });

  const catTotals = {};
  let totalSpending = 0;

  expenses.forEach((exp) => {
    const amt = parseFloat(exp.amount || 0);
    totalSpending += amt;
    catTotals[exp.categoryId] = (catTotals[exp.categoryId] || 0) + amt;
  });

  const remainingBudget = monthlyBudget - totalSpending;
  const isOverBudget = remainingBudget < 0;

  const categoryRowsHTML = Object.entries(catTotals)
    .sort(([, a], [, b]) => b - a)
    .map(([catId, total]) => {
      const cat = getCategoryById(catId);
      const percentage = totalSpending > 0 ? ((total / totalSpending) * 100).toFixed(1) : '0.0';
      return `
        <tr>
          <td style="padding: 10px 14px; border-bottom: 1px solid #E8EFE8;">
            <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background-color: ${cat.color}; margin-right: 8px;"></span>
            <strong>${cat.name}</strong>
          </td>
          <td style="padding: 10px 14px; border-bottom: 1px solid #E8EFE8; text-align: right;">${percentage}%</td>
          <td style="padding: 10px 14px; border-bottom: 1px solid #E8EFE8; text-align: right; font-weight: 600; color: #2D4233;">
            ${currencySymbol}${total.toFixed(2)}
          </td>
        </tr>
      `;
    })
    .join('');

  const transactionRowsHTML = expenses
    .slice(0, 100)
    .map((exp) => {
      const cat = getCategoryById(exp.categoryId);
      return `
        <tr>
          <td style="padding: 10px 14px; border-bottom: 1px solid #F0F4F0; color: #556B5B;">${exp.date}</td>
          <td style="padding: 10px 14px; border-bottom: 1px solid #F0F4F0;">
            <span style="background: #EAF1EB; color: #36533D; padding: 3px 8px; border-radius: 6px; font-size: 12px; font-weight: 500;">
              ${cat.name}
            </span>
          </td>
          <td style="padding: 10px 14px; border-bottom: 1px solid #F0F4F0; color: #333;">${exp.note || '—'}</td>
          <td style="padding: 10px 14px; border-bottom: 1px solid #F0F4F0; text-align: center; color: #778;">
            ${exp.receiptUri ? '📎 Attached' : '—'}
          </td>
          <td style="padding: 10px 14px; border-bottom: 1px solid #F0F4F0; text-align: right; font-weight: 600; color: #2D4233;">
            ${currencySymbol}${parseFloat(exp.amount).toFixed(2)}
          </td>
        </tr>
      `;
    })
    .join('');

  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8" />
        <title>SereneSpend Financial Report</title>
        <style>
          body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: #FAFCFA;
            color: #233226;
            margin: 0;
            padding: 32px;
          }
          .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid #DCE6DD;
            padding-bottom: 20px;
            margin-bottom: 24px;
          }
          .brand-title {
            font-size: 24px;
            font-weight: 700;
            color: #36533D;
            letter-spacing: -0.5px;
          }
          .brand-sub {
            font-size: 13px;
            color: #6C8271;
            margin-top: 4px;
          }
          .meta-box {
            text-align: right;
            font-size: 13px;
            color: #6C8271;
          }
          .summary-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 14px;
            margin-bottom: 28px;
          }
          .card {
            background: #FFFFFF;
            border: 1px solid #E1EAE1;
            border-radius: 12px;
            padding: 16px;
          }
          .card-label {
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: #6C8271;
            margin-bottom: 6px;
          }
          .card-value {
            font-size: 20px;
            font-weight: 700;
            color: #233226;
          }
          .section-title {
            font-size: 16px;
            font-weight: 600;
            color: #36533D;
            margin: 24px 0 12px 0;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            background: #FFFFFF;
            border: 1px solid #E1EAE1;
            border-radius: 10px;
            overflow: hidden;
            font-size: 13px;
          }
          th {
            background: #F0F5F0;
            color: #4A6350;
            text-align: left;
            padding: 10px 14px;
            font-weight: 600;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.4px;
          }
          .footer {
            margin-top: 36px;
            padding-top: 16px;
            border-top: 1px solid #E5EEE5;
            text-align: center;
            font-size: 12px;
            color: #8BA090;
          }
        </style>
      </head>
      <body>
        <div class="header">
          <div>
            <div class="brand-title">🌿 SereneSpend</div>
            <div class="brand-sub">Peaceful Expense Tracker & Budget Calculator</div>
          </div>
          <div class="meta-box">
            <div><strong>Report Period:</strong> ${monthName} ${year}</div>
            <div><strong>Generated:</strong> ${generatedAt}</div>
            <div><strong>Privacy:</strong> 100% Offline</div>
          </div>
        </div>

        <div class="summary-grid">
          <div class="card">
            <div class="card-label">Monthly Budget</div>
            <div class="card-value">${currencySymbol}${monthlyBudget.toFixed(2)}</div>
          </div>
          <div class="card">
            <div class="card-label">Total Spent</div>
            <div class="card-value">${currencySymbol}${totalSpending.toFixed(2)}</div>
          </div>
          <div class="card">
            <div class="card-label">${isOverBudget ? 'Over Budget' : 'Remaining Budget'}</div>
            <div class="card-value" style="color: ${isOverBudget ? '#C75D5D' : '#3C8A53'};">
              ${currencySymbol}${Math.abs(remainingBudget).toFixed(2)}
            </div>
          </div>
          <div class="card">
            <div class="card-label">Transactions</div>
            <div class="card-value">${expenses.length}</div>
          </div>
        </div>

        <div class="section-title">📊 Spending by Category</div>
        <table>
          <thead>
            <tr>
              <th>Category</th>
              <th style="text-align: right;">Share</th>
              <th style="text-align: right;">Total Amount</th>
            </tr>
          </thead>
          <tbody>
            ${categoryRowsHTML || '<tr><td colspan="3" style="padding:14px; text-align:center;">No expenses recorded for this period.</td></tr>'}
          </tbody>
        </table>

        <div class="section-title">📝 Transaction History (${expenses.length} items)</div>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Category</th>
              <th>Description / Note</th>
              <th style="text-align: center;">Receipt</th>
              <th style="text-align: right;">Amount</th>
            </tr>
          </thead>
          <tbody>
            ${transactionRowsHTML || '<tr><td colspan="5" style="padding:14px; text-align:center;">No transactions found.</td></tr>'}
          </tbody>
        </table>

        <div class="footer">
          Generated entirely offline on device with <strong>SereneSpend</strong>. No cloud servers, no account required.
        </div>
      </body>
    </html>
  `;
};

export const printExpenseReport = async (reportData) => {
  try {
    const html = generateReportHTML(reportData);
    await Print.printAsync({ html });
    return { success: true };
  } catch (error) {
    console.error('Failed to print PDF:', error);
    throw error;
  }
};

export const exportExpenseReportPDF = async (reportData) => {
  try {
    const html = generateReportHTML(reportData);
    const { uri } = await Print.printToFileAsync({ html });
    const isAvailable = await Sharing.isAvailableAsync();
    if (isAvailable) {
      await Sharing.shareAsync(uri, {
        UTI: '.pdf',
        mimeType: 'application/pdf',
        dialogTitle: 'Share or Save SereneSpend Report (PDF)',
      });
    }
    return { success: true, uri };
  } catch (error) {
    console.error('Failed to export PDF file:', error);
    throw error;
  }
};
