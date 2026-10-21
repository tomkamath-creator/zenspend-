package com.vk.zenspend.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vk.zenspend.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Query("SELECT * FROM debts ORDER BY isSettled ASC, timestamp DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE isSettled = 0 ORDER BY timestamp DESC")
    fun getActiveDebts(): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Query("UPDATE debts SET isSettled = 1, settledDate = :settledDate WHERE id = :debtId")
    suspend fun settleDebt(debtId: Long, settledDate: Long)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)
}
