package com.vk.zenspend.data.repository

import com.vk.zenspend.data.local.dao.EmiDao
import com.vk.zenspend.data.mapper.toDomain
import com.vk.zenspend.data.mapper.toEntity
import com.vk.zenspend.domain.model.Emi
import com.vk.zenspend.domain.repository.EmiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmiRepositoryImpl @Inject constructor(
    private val emiDao: EmiDao
) : EmiRepository {

    override fun getAllEmis(): Flow<List<Emi>> {
        return emiDao.getAllEmis().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertEmi(emi: Emi): Long {
        return emiDao.insertEmi(emi.toEntity())
    }

    override suspend fun updateEmi(emi: Emi) {
        emiDao.updateEmi(emi.toEntity())
    }

    override suspend fun setEmiPaidStatus(id: Long, isPaid: Boolean) {
        emiDao.setEmiPaidStatus(id, isPaid)
    }

    override suspend fun deleteEmi(emi: Emi) {
        emiDao.deleteEmi(emi.toEntity())
    }

    override fun getTotalMonthlyEmiBurden(): Flow<Double> {
        return emiDao.getTotalMonthlyEmiBurden().map { it ?: 0.0 }
    }
}
