package com.vk.zenspend.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.vk.zenspend.domain.model.DebtType

@Entity(
    tableName = "debts",
    indices = [
        Index(value = ["isSettled"]),
        Index(value = ["personName"])
    ]
)
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
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
