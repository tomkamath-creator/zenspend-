package com.vk.zenspend.domain.model

data class Budget(
    val id: Long = 0,
    val month: Int,
    val year: Int,
    val totalMonthlyBudget: Double,
    val calmDailyTarget: Double = 0.0,
    val carryOverSurplus: Boolean = true
)
