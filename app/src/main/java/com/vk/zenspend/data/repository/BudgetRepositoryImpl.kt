package com.vk.zenspend.data.repository

import com.vk.zenspend.data.local.dao.BudgetDao
import com.vk.zenspend.data.mapper.toDomain
import com.vk.zenspend.data.mapper.toEntity
import com.vk.zenspend.domain.model.Budget
import com.vk.zenspend.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getBudgetForMonth(month: Int, year: Int): Flow<Budget?> {
        return budgetDao.getBudgetForMonth(month, year).map { it?.toDomain() }
    }

    override fun getCurrentBudget(): Flow<Budget?> {
        return budgetDao.getLatestBudget().map { it?.toDomain() }
    }

    override suspend fun upsertBudget(budget: Budget) {
        budgetDao.upsertBudget(budget.toEntity())
    }
}
