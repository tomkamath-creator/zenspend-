package com.vk.zenspend.domain.model

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: Long,
    val type: TransactionType = TransactionType.EXPENSE,
    val isDefault: Boolean = true,
    val monthlyBudgetLimit: Double? = null
)
