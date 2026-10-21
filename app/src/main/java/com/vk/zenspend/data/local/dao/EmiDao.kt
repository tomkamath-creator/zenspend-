package com.vk.zenspend.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vk.zenspend.data.local.entity.EmiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmiDao {

    @Query("SELECT * FROM emis ORDER BY isPaidThisMonth ASC, dueDayOfMonth ASC")
    fun getAllEmis(): Flow<List<EmiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmi(emi: EmiEntity): Long

    @Update
    suspend fun updateEmi(emi: EmiEntity)

    @Query("UPDATE emis SET isPaidThisMonth = :isPaid WHERE id = :id")
    suspend fun setEmiPaidStatus(id: Long, isPaid: Boolean)

    @Delete
    suspend fun deleteEmi(emi: EmiEntity)

    @Query("SELECT SUM(monthlyAmount) FROM emis")
    fun getTotalMonthlyEmiBurden(): Flow<Double?>
}
