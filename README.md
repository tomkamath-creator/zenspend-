# 🌿 ZenSpend • by VK

> A calm, distraction-free, offline-first personal finance app designed to make money management feel peaceful and effortless.

Built with **100% Jetpack Compose**, **Material 3**, **Clean Architecture**, **Room SQLite**, and **Hilt DI with KSP**.

---

## 🧘 Calm Design System (Light Mode)

- **Base Background**: Warm porcelain & eggshell white (`#F8F9FA` / `#F3F4F6`), never harsh or blinding.
- **Surface Cards**: Pure white (`#FFFFFF`) with subtle layered ambient elevation and soft **24dp rounded corners**.
- **Mood-Tuned Color Palette**:
  - 🟢 **Calm Sage Green** (`#34C759` / `#2D6A4F`): Healthy budget & savings.
  - 🟠 **Warm Apricot / Amber** (`#FF9500`): Cautionary thresholds (>75% budget).
  - 🔴 **Muted Terracotta / Soft Coral** (`#FF453A`): Exceeded budget limits.
  - 🟣 **Soft Lavender** (`#AF52DE`): Rituals & growth categories.
  - 🔵 **Sky Blue** (`#007AFF`): Transit & essentials.
- **Motion Language**:
  - Spring physics: `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)`
  - Tactile and fluid feedback on every tap.

---

## 🏛️ Architecture & Tech Stack

```
ZenSpend
 ├── app
 │    ├── src/main/java/com/vk/zenspend
 │    │    ├── data/
 │    │    │    ├── local/         (Room Database, DAOs, Entities, Converters)
 │    │    │    ├── mapper/        (Entity <-> Domain Mappers)
 │    │    │    └── repository/    (Offline Repository Implementations)
 │    │    ├── domain/
 │    │    │    ├── model/         (Transaction, Category, Budget, Debt)
 │    │    │    └── repository/    (Domain Repository Contracts)
 │    │    ├── di/                 (Hilt Database & Repository Modules)
 │    │    └── ui/
 │    │         ├── theme/         (Color, Type, Shape, Motion, Theme)
 │    │         ├── components/    (ZenCard, VkBadge)
 │    │         └── screens/       (ZenSpendViewModel, ZenSpendRootScreen)
 │    └── src/main/res/            (Zen adaptive icon, themes, strings)
 ├── gradle/
 │    ├── libs.versions.toml       (Modern Gradle Version Catalog)
 │    └── wrapper/                 (Gradle 8.11.1 Wrapper)
 └── .github/workflows/
      └── main.yml                 (Automated APK CI/CD Build)
```

---

## 📋 Phase Roadmap

- [x] **Phase 1: Foundation (Current)**
  - Modern Gradle Version Catalog (`libs.versions.toml`)
  - Room SQLite Schema (`Transaction`, `Category`, `Budget`, `Debt`)
  - Hilt Dependency Injection with KSP
  - Scandinavian Zen Light Theme Tokens (Porcelain, Sage, Apricot, Terracotta, 24dp shapes, Spring Motion)
  - Prepopulated Mindful Categories (Dining, Living, Wellness, Transit, Growth, Rituals, Play, Income)
  - Custom Zen Balance Icon with "VK" signature
  - GitHub Actions APK Build Workflow (`.github/workflows/main.yml`)
- [ ] **Phase 2: Zen Dashboard & Custom Calculator Numpad**
  - Integrated glassmorphic custom numpad with inline math engine (`120 + 35 * 2`)
  - Rolling Digital Ticker (odometer number rolling)
  - Rapid transaction entry bottom sheet
- [ ] **Phase 3: Peaceful Analytics & Spending Runway**
  - Daily Calm Allowance indicator
  - Interactive scrubbable Spring Donut & Bézier charts
  - Biometric Privacy Vault & "Hide Balances" frosted mode
- [ ] **Phase 4: ML Kit Offline Receipt Scanner & Peer IOU Ledger**
  - On-Device OCR via Google ML Kit
  - Lightweight "Lent & Borrowed" peer ledger with settle-up animation
- [ ] **Phase 5: Backup/Restore & Final Polish**
  - Encrypted JSON Export/Import & optional Drive backup
  - WorkManager daily gentle summary notification

---

## 🚀 Pushing to GitHub & Building the APK

Run these commands in PowerShell or your terminal:

```powershell
# 1. Check current status
git status

# 2. Stage all Phase 1 files
git add .

# 3. Commit Phase 1 architecture
git commit -m "feat: complete Phase 1 ZenSpend architecture with Room, Hilt, Zen Light Theme, and GitHub Actions"

# 4. Link your GitHub repository (if not already linked)
git remote add origin https://github.com/<your-username>/<your-repo-name>.git

# 5. Push to GitHub
git branch -M main
git push -u origin main
```

Once pushed, GitHub Actions automatically triggers `.github/workflows/main.yml`, builds `ZenSpend-debug.apk`, and uploads it to the workflow **Artifacts** tab.
