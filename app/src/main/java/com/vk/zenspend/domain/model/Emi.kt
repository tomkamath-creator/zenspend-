package com.vk.zenspend.domain.model

data class Emi(
    val id: Long = 0,
    val title: String,            // e.g. "iPhone 15 EMI", "Home Loan", "Bike EMI"
    val monthlyAmount: Double,    // e.g. 2499.0
    val dueDayOfMonth: Int,       // e.g. 5 (5th of every month)
    val totalTenureMonths: Int? = null,
    val remainingMonths: Int? = null,
    val isPaidThisMonth: Boolean = false,
    val notes: String? = null
)
