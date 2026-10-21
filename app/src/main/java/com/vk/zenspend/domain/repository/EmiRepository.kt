package com.vk.zenspend.domain.repository

import com.vk.zenspend.domain.model.Emi
import kotlinx.coroutines.flow.Flow

interface EmiRepository {
    fun getAllEmis(): Flow<List<Emi>>
    suspend fun insertEmi(emi: Emi): Long
    suspend fun updateEmi(emi: Emi)
    suspend fun setEmiPaidStatus(id: Long, isPaid: Boolean)
    suspend fun deleteEmi(emi: Emi)
    fun getTotalMonthlyEmiBurden(): Flow<Double>
}
