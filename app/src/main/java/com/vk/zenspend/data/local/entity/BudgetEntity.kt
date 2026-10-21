package com.vk.zenspend.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["month", "year"], unique = true)
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val month: Int,
    val year: Int,
    val totalMonthlyBudget: Double,
    val calmDailyTarget: Double = 0.0,
    val carryOverSurplus: Boolean = true
)
