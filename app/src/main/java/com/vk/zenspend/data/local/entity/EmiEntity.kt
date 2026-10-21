package com.vk.zenspend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emis")
data class EmiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val monthlyAmount: Double,
    val dueDayOfMonth: Int,
    val totalTenureMonths: Int? = null,
    val remainingMonths: Int? = null,
    val isPaidThisMonth: Boolean = false,
    val notes: String? = null
)
