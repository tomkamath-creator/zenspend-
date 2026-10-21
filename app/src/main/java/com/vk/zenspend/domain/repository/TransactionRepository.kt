package com.vk.zenspend.domain.repository

import com.vk.zenspend.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>>
    fun getTransactionById(id: Long): Flow<Transaction?>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteTransactionById(id: Long)
    fun getTotalExpensesBetween(start: Long, end: Long): Flow<Double>
    fun getTotalIncomeBetween(start: Long, end: Long): Flow<Double>
}
