package com.vk.zenspend.domain.model

data class Transaction(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val timestamp: Long,
    val note: String? = null,
    val receiptImagePath: String? = null,
    val isExcludedFromCalmAllowance: Boolean = false
)
