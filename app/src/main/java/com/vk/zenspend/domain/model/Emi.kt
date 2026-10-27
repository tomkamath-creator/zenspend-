package com.vk.zenspend.domain.model

data class Emi(
    val id: Long = 0,
    val title: String,            // e.g. "HDFC Top 100 SIP", "Home Loan", "House Rent"
    val monthlyAmount: Double,    // e.g. 5000.0
    val dueDayOfMonth: Int,       // e.g. 5 (5th of every month)
    val totalTenureMonths: Int? = null,
    val remainingMonths: Int? = null,
    val isPaidThisMonth: Boolean = false,
    val notes: String? = null,
    val commitmentType: String = "EMI" // "EMI", "MUTUAL_FUND", "BILL"
)
