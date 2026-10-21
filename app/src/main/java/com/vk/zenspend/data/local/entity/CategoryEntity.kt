package com.vk.zenspend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vk.zenspend.domain.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: Long,
    val type: TransactionType = TransactionType.EXPENSE,
    val isDefault: Boolean = true,
    val monthlyBudgetLimit: Double? = null
)
