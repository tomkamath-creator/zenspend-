package com.vk.zenspend.data.repository

import com.vk.zenspend.data.local.dao.TransactionDao
import com.vk.zenspend.data.mapper.toDomain
import com.vk.zenspend.data.mapper.toEntity
import com.vk.zenspend.domain.model.Transaction
import com.vk.zenspend.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetween(start, end).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTransactionById(id: Long): Flow<Transaction?> {
        return transactionDao.getTransactionById(id).map { it?.toDomain() }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    override fun getTotalExpensesBetween(start: Long, end: Long): Flow<Double> {
        return transactionDao.getTotalExpensesBetween(start, end).map { it ?: 0.0 }
    }

    override fun getTotalIncomeBetween(start: Long, end: Long): Flow<Double> {
        return transactionDao.getTotalIncomeBetween(start, end).map { it ?: 0.0 }
    }
}
