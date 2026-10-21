package com.vk.zenspend.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.zenspend.domain.model.Budget
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.model.TransactionType
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class ZenSpendViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val debtRepository: DebtRepository,
    private val emiRepository: EmiRepository
) : ViewModel() {

    // 1-second splash screen control
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    fun dismissSplash() {
        _isSplashVisible.value = false
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

    // Current month start & end timestamps
    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    private val startOfMonth: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val endOfMonth: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val totalExpensesThisMonth: StateFlow<Double> = transactionRepository
        .getTotalExpensesBetween(startOfMonth, endOfMonth)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalIncomeThisMonth: StateFlow<Double> = transactionRepository
        .getTotalIncomeBetween(startOfMonth, endOfMonth)
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

    val monthlyBudgetAmount: StateFlow<Double> = currentBudget
        .combine(totalExpensesThisMonth) { budget, _ ->
            budget?.totalMonthlyBudget ?: 25000.0 // Default calm target of ₹25,000
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 25000.0
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
        val remainingBudget = max(0.0, budget - spent)
        remainingBudget / remainingDaysInMonth
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 25000.0 / remainingDaysInMonth
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
                        categoryName = "Calm Essentials",
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
