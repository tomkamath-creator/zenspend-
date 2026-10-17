# 🌿 SereneSpend — Offline Expense Tracker & Budget Calculator

A peaceful, stress-free mobile app for tracking expenses, managing budgets, and scanning receipts. Built with **React Native & Expo**, SereneSpend is **100% free, 100% offline, requires zero account sign-ups, and incurs zero cloud fees**.

---

## ⚡ Important for GitHub Upload: Why this project is under 35 files
GitHub's web uploader has a strict limit of **100 files at a time**.
This repository contains **only 32 files in total** because:
1. `node_modules` is excluded via `.gitignore` (as standard in all JavaScript/React projects).
2. GitHub Actions (`.github/workflows/build-android.yml`) automatically installs dependencies and builds your Android APK in the cloud for free.

---

## ✨ Features

- **Peaceful Light Theme:** Soft off-white backgrounds (`#F7F9F6`), gentle sage green highlights (`#4E7257`), and clean white cards (`#FFFFFF`).
- **Offline Bill / Screenshot Scanner:** Take a photo or pick a screenshot; SereneSpend scans totals and dates offline and keeps the photo saved in your device storage.
- **Real-Time Budget Math:** Set a monthly budget, automatically subtract expenses, and view real-time totals for:
  - 📅 **Today (Daily)**
  - 🗓️ **This Month (Monthly)**
  - 📈 **This Year (Yearly)**
- **Safe Daily Allowance:** Intelligently calculates how much you can safely spend per day for the remaining days of the month.
- **Calendar Spending Heatmap:** Visual monthly calendar grid shaded by spending intensity in gentle sage green. Tap any day to inspect transactions.
- **Printable PDF Reports:** 100% offline financial statements with direct wireless printing or PDF saving/sharing.
- **Spreadsheet (CSV) Export:** One-tap export to open in Excel, Numbers, or Google Sheets.
- **App Lock:** Protect your financial privacy with your phone's **Fingerprint**, **Face ID**, or a **4-digit PIN**.

---

## 📱 How to Build the APK on GitHub ($0)

1. Upload or push this folder to your GitHub repository.
2. Go to the **Actions** tab in GitHub.
3. Select **"Build Android APK (SereneSpend)"** and click **"Run workflow"**.
4. Once finished, download `SereneSpend.apk` from the **Artifacts** section and install it on your Android phone!
