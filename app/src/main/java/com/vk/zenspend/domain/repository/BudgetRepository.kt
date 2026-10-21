package com.vk.zenspend.domain.repository

import com.vk.zenspend.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetForMonth(month: Int, year: Int): Flow<Budget?>
    fun getCurrentBudget(): Flow<Budget?>
    suspend fun upsertBudget(budget: Budget)
}
