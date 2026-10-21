package com.vk.zenspend.domain.model

data class Debt(
    val id: Long = 0,
    val personName: String,
    val amount: Double,
    val type: DebtType,
    val title: String,
    val timestamp: Long,
    val dueDate: Long? = null,
    val isSettled: Boolean = false,
    val settledDate: Long? = null,
    val notes: String? = null
)
