package com.vk.zenspend.domain.repository

import com.vk.zenspend.domain.model.Debt
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    fun getAllDebts(): Flow<List<Debt>>
    fun getActiveDebts(): Flow<List<Debt>>
    suspend fun insertDebt(debt: Debt): Long
    suspend fun updateDebt(debt: Debt)
    suspend fun settleDebt(debtId: Long, settledDate: Long)
    suspend fun deleteDebt(debt: Debt)
}
