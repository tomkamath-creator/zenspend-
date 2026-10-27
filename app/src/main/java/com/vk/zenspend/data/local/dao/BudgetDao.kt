package com.vk.zenspend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vk.zenspend.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year LIMIT 1")
    fun getBudgetForMonth(month: Int, year: Int): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC LIMIT 1")
    fun getLatestBudget(): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets")
    suspend fun getAllBudgetsList(): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudgets(budgets: List<BudgetEntity>)
}
