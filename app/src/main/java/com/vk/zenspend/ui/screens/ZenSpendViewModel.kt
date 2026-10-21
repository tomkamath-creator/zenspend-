package com.vk.zenspend.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.repository.BudgetRepository
import com.vk.zenspend.domain.repository.CategoryRepository
import com.vk.zenspend.domain.repository.DebtRepository
import com.vk.zenspend.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ZenSpendViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val debtRepository: DebtRepository
) : ViewModel() {

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
}
