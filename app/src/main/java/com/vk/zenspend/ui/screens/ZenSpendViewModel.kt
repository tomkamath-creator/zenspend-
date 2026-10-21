package com.vk.zenspend.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.zenspend.data.repository.UserPreferencesRepository
import com.vk.zenspend.domain.model.Budget
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.model.CategorySpend
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.domain.model.SpendComparison
import com.vk.zenspend.domain.model.SpendPeriod
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.model.TransactionType
import com.vk.zenspend.domain.model.TrendPoint
import com.vk.zenspend.domain.repository.BudgetRepository
import com.vk.zenspend.domain.repository.CategoryRepository
import com.vk.zenspend.domain.repository.DebtRepository
import com.vk.zenspend.domain.repository.EmiRepository
import com.vk.zenspend.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

@HiltViewModel
class ZenSpendViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val debtRepository: DebtRepository,
    private val emiRepository: EmiRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // 1-second splash screen control
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    fun dismissSplash() {
        _isSplashVisible.value = false
    }

    // User Profile & Onboarding
    val userName: StateFlow<String> = userPreferencesRepository.userName
    val monthlySalary: StateFlow<Double> = userPreferencesRepository.monthlySalary
    val hasCompletedOnboarding: StateFlow<Boolean> = userPreferencesRepository.hasCompletedOnboarding
    val isDarkMode: StateFlow<Boolean?> = userPreferencesRepository.isDarkMode

    fun toggleDarkMode(currentIsDark: Boolean) {
        userPreferencesRepository.toggleDarkMode(currentIsDark)
    }

    fun completeOnboarding(name: String, salary: Double, budget: Double, initialEmi: Emi?) {
        viewModelScope.launch {
            userPreferencesRepository.saveUserProfile(name, salary)
            if (budget > 0.0) {
                setMonthlyBudget(budget)
            }
            if (initialEmi != null) {
                emiRepository.insertEmi(initialEmi)
            }
            if (salary > 0.0) {
                transactionRepository.insertTransaction(
                    Transaction(
                        title = "Monthly Salary Income",
                        amount = salary,
                        type = TransactionType.INCOME,
                        categoryId = "focus_growth",
                        categoryName = "Focus & Growth",
                        categoryIcon = "Savings",
                        categoryColor = 0xFF48BB78L,
                        timestamp = System.currentTimeMillis(),
                        note = "Recorded during initial onboarding setup"
                    )
                )
            }
            userPreferencesRepository.setOnboardingCompleted(true)
        }
    }

    fun restartOnboarding() {
        userPreferencesRepository.resetOnboarding()
    }

    val categories: StateFlow<List<Category>> = categoryRepository
        .getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactions: StateFlow<List<Transaction>> = transactionRepository
        .getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current calendar boundaries
    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val startOfMonth: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfMonth: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    private val startOfLastMonth: Long = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val endOfLastMonth: Long = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val startOfYear: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfYear: Long = Calendar.getInstance().apply {
        set(Calendar.MONTH, Calendar.DECEMBER)
        set(Calendar.DAY_OF_MONTH, 31)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    private val startOfLastYear: Long = Calendar.getInstance().apply {
        add(Calendar.YEAR, -1)
        set(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val endOfLastYear: Long = Calendar.getInstance().apply {
        add(Calendar.YEAR, -1)
        set(Calendar.MONTH, Calendar.DECEMBER)
        set(Calendar.DAY_OF_MONTH, 31)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val totalExpensesThisMonth: StateFlow<Double> = transactionRepository
        .getTotalExpensesBetween(startOfMonth, endOfMonth)
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalIncomeThisMonth: StateFlow<Double> = transactionRepository
        .getTotalIncomeBetween(startOfMonth, endOfMonth)
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val currentBudget: StateFlow<Budget?> = budgetRepository
        .getCurrentBudget()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Initial budget is 0.00 until set by user
    val monthlyBudgetAmount: StateFlow<Double> = currentBudget
        .map { it?.totalMonthlyBudget ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val remainingDaysInMonth: Int = run {
        val cal = Calendar.getInstance()
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        max(1, totalDays - currentDay + 1)
    }

    // Daily Calm Allowance in ₹
    val dailyCalmAllowance: StateFlow<Double> = combine(
        monthlyBudgetAmount,
        totalExpensesThisMonth
    ) { budget, spent ->
        if (budget <= 0.0) {
            0.0
        } else {
            val remainingBudget = max(0.0, budget - spent)
            remainingBudget / remainingDaysInMonth
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // EMIs
    val emis: StateFlow<List<Emi>> = emiRepository
        .getAllEmis()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalMonthlyEmiBurden: StateFlow<Double> = emiRepository
        .getTotalMonthlyEmiBurden()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Search & Filter
    val searchQuery = MutableStateFlow("")

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        searchQuery
    ) { txList, query ->
        if (query.isBlank()) {
            txList
        } else {
            txList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.categoryName.contains(query, ignoreCase = true) ||
                        (it.note?.contains(query, ignoreCase = true) == true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Group transactions by date headers: "Today", "Yesterday", or "Oct 21"
    val groupedTransactions: StateFlow<Map<String, List<Transaction>>> = filteredTransactions
        .mapGroupedByDate()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Analytics: Period Toggle (Monthly vs Yearly)
    val selectedSpendPeriod = MutableStateFlow(SpendPeriod.THIS_MONTH)

    fun setSpendPeriod(period: SpendPeriod) {
        selectedSpendPeriod.value = period
    }

    // Monthly Spend Comparison (This Month vs Last Month)
    val monthlySpendComparison: StateFlow<SpendComparison> = transactions.map { txList ->
        val thisMonthSpent = txList
            .filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfMonth..endOfMonth }
            .sumOf { it.amount }

        val lastMonthSpent = txList
            .filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfLastMonth..endOfLastMonth }
            .sumOf { it.amount }

        val diff = abs(thisMonthSpent - lastMonthSpent)
        val isMore = thisMonthSpent > lastMonthSpent
        val deltaPct = if (lastMonthSpent > 0) ((thisMonthSpent - lastMonthSpent) / lastMonthSpent) * 100 else 0.0

        val summary = when {
            lastMonthSpent == 0.0 && thisMonthSpent > 0 -> "First active tracking month on ZenSpend"
            isMore -> String.format(Locale.getDefault(), "↑ %.1f%% more spent than last month (+₹%,.0f)", deltaPct, diff)
            thisMonthSpent < lastMonthSpent -> String.format(Locale.getDefault(), "↓ %.1f%% less spent than last month (Saved ₹%,.0f)", abs(deltaPct), diff)
            else -> "Spending pace is steady compared to last month"
        }

        SpendComparison(
            currentPeriodAmount = thisMonthSpent,
            previousPeriodAmount = lastMonthSpent,
            deltaPercentage = deltaPct,
            isMoreSpent = isMore,
            differenceAmount = diff,
            summaryText = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SpendComparison(0.0, 0.0, 0.0, false, 0.0, "Ready to analyze")
    )

    // Yearly Spend Comparison (This Year vs Last Year)
    val yearlySpendComparison: StateFlow<SpendComparison> = transactions.map { txList ->
        val thisYearSpent = txList
            .filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfYear..endOfYear }
            .sumOf { it.amount }

        val lastYearSpent = txList
            .filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfLastYear..endOfLastYear }
            .sumOf { it.amount }

        val diff = abs(thisYearSpent - lastYearSpent)
        val isMore = thisYearSpent > lastYearSpent
        val deltaPct = if (lastYearSpent > 0) ((thisYearSpent - lastYearSpent) / lastYearSpent) * 100 else 0.0

        val summary = when {
            lastYearSpent == 0.0 && thisYearSpent > 0 -> "Year-to-date tracking in progress"
            isMore -> String.format(Locale.getDefault(), "↑ %.1f%% higher than last year (+₹%,.0f)", deltaPct, diff)
            thisYearSpent < lastYearSpent -> String.format(Locale.getDefault(), "↓ %.1f%% lower than last year (Saved ₹%,.0f)", abs(deltaPct), diff)
            else -> "Annual spending trajectory is balanced"
        }

        SpendComparison(
            currentPeriodAmount = thisYearSpent,
            previousPeriodAmount = lastYearSpent,
            deltaPercentage = deltaPct,
            isMoreSpent = isMore,
            differenceAmount = diff,
            summaryText = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SpendComparison(0.0, 0.0, 0.0, false, 0.0, "Ready to analyze")
    )

    // Monthly Trend Points (Day by Day for Bézier Curve)
    val monthlyTrendPoints: StateFlow<List<TrendPoint>> = transactions.map { txList ->
        val cal = Calendar.getInstance()
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dailyMap = mutableMapOf<Int, Double>()

        txList.filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfMonth..endOfMonth }
            .forEach { tx ->
                cal.timeInMillis = tx.timestamp
                val day = cal.get(Calendar.DAY_OF_MONTH)
                dailyMap[day] = (dailyMap[day] ?: 0.0) + tx.amount
            }

        (1..daysInMonth).map { day ->
            TrendPoint(
                label = "Day $day",
                value = dailyMap[day] ?: 0.0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Yearly Trend Points (12 Months for Bézier Curve)
    val yearlyTrendPoints: StateFlow<List<TrendPoint>> = transactions.map { txList ->
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthlyMap = mutableMapOf<Int, Double>()
        val cal = Calendar.getInstance()

        txList.filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfYear..endOfYear }
            .forEach { tx ->
                cal.timeInMillis = tx.timestamp
                val m = cal.get(Calendar.MONTH) // 0-11
                monthlyMap[m] = (monthlyMap[m] ?: 0.0) + tx.amount
            }

        (0..11).map { m ->
            TrendPoint(
                label = monthNames[m],
                value = monthlyMap[m] ?: 0.0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Category Spend Breakdown for Morphing Donut Chart
    val categorySpendBreakdown: StateFlow<List<CategorySpend>> = combine(
        transactions,
        selectedSpendPeriod,
        categories
    ) { txList, period, catList ->
        val (rangeStart, rangeEnd) = if (period == SpendPeriod.THIS_MONTH) {
            startOfMonth to endOfMonth
        } else {
            startOfYear to endOfYear
        }

        val periodExpenses = txList.filter {
            it.type == TransactionType.EXPENSE && it.timestamp in rangeStart..rangeEnd
        }

        val totalPeriodSpent = periodExpenses.sumOf { it.amount }
        if (totalPeriodSpent <= 0.0) {
            emptyList()
        } else {
            val groupMap = periodExpenses.groupBy { it.categoryId }
            groupMap.map { (catId, items) ->
                val sum = items.sumOf { it.amount }
                val catMeta = catList.find { it.id == catId }
                val name = catMeta?.name ?: items.firstOrNull()?.categoryName ?: "Other"
                val colorHex = catMeta?.colorHex ?: items.firstOrNull()?.categoryColor ?: 0xFF48BB78L
                val icon = catMeta?.iconName ?: items.firstOrNull()?.categoryIcon ?: "Spa"

                CategorySpend(
                    categoryId = catId,
                    categoryName = name,
                    colorHex = colorHex,
                    iconName = icon,
                    amount = sum,
                    percentage = (sum / totalPeriodSpent).toFloat()
                )
            }.sortedByDescending { it.amount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun setMonthlyBudget(amount: Double) {
        viewModelScope.launch {
            budgetRepository.upsertBudget(
                Budget(
                    month = currentMonth,
                    year = currentYear,
                    totalMonthlyBudget = amount
                )
            )
        }
    }

    fun addEmi(emi: Emi) {
        viewModelScope.launch {
            emiRepository.insertEmi(emi)
        }
    }

    fun deleteEmi(emi: Emi) {
        viewModelScope.launch {
            emiRepository.deleteEmi(emi)
        }
    }

    fun toggleEmiPaid(emi: Emi) {
        viewModelScope.launch {
            val newStatus = !emi.isPaidThisMonth
            emiRepository.setEmiPaidStatus(emi.id, newStatus)
            // If marked as paid, automatically log this EMI as an expense transaction!
            if (newStatus) {
                transactionRepository.insertTransaction(
                    Transaction(
                        title = "EMI: ${emi.title}",
                        amount = emi.monthlyAmount,
                        type = TransactionType.EXPENSE,
                        categoryId = "calm_essentials",
                        categoryName = "Bills & Utilities",
                        categoryIcon = "Payments",
                        categoryColor = 0xFFED8936L,
                        timestamp = System.currentTimeMillis(),
                        note = "Automatic EMI payment logged"
                    )
                )
            }
        }
    }

    private fun StateFlow<List<Transaction>>.mapGroupedByDate(): StateFlow<Map<String, List<Transaction>>> {
        return combine(this, totalExpensesThisMonth) { txList, _ ->
            val todayCal = Calendar.getInstance()
            val txCal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

            txList.groupBy { tx ->
                txCal.timeInMillis = tx.timestamp
                when {
                    isSameDay(todayCal, txCal) -> "Today"
                    isYesterday(todayCal, txCal) -> "Yesterday"
                    else -> dateFormat.format(Date(tx.timestamp))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(today: Calendar, other: Calendar): Boolean {
        val clone = today.clone() as Calendar
        clone.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(clone, other)
    }
}
