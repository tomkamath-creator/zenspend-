package com.vk.zenspend.data.repository

import com.vk.zenspend.data.local.dao.DebtDao
import com.vk.zenspend.data.mapper.toDomain
import com.vk.zenspend.data.mapper.toEntity
import com.vk.zenspend.domain.model.Debt
import com.vk.zenspend.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepositoryImpl @Inject constructor(
    private val debtDao: DebtDao
) : DebtRepository {

    override fun getAllDebts(): Flow<List<Debt>> {
        return debtDao.getAllDebts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getActiveDebts(): Flow<List<Debt>> {
        return debtDao.getActiveDebts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertDebt(debt: Debt): Long {
        return debtDao.insertDebt(debt.toEntity())
    }

    override suspend fun updateDebt(debt: Debt) {
        debtDao.updateDebt(debt.toEntity())
    }

    override suspend fun settleDebt(debtId: Long, settledDate: Long) {
        debtDao.settleDebt(debtId, settledDate)
    }

    override suspend fun deleteDebt(debt: Debt) {
        debtDao.deleteDebt(debt.toEntity())
    }
}
