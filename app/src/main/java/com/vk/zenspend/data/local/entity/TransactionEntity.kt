package com.vk.zenspend.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vk.zenspend.domain.model.TransactionType

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["categoryId"]),
        Index(value = ["type"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
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
