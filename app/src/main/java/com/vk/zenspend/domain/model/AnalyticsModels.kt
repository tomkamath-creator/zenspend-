package com.vk.zenspend.domain.model

enum class SpendPeriod {
    THIS_MONTH,
    THIS_YEAR
}

data class CategorySpend(
    val categoryId: String,
    val categoryName: String,
    val colorHex: Long,
    val iconName: String,
    val amount: Double,
    val percentage: Float
)

data class TrendPoint(
    val label: String,
    val value: Double,
    val secondaryLabel: String = ""
)

data class SpendComparison(
    val currentPeriodAmount: Double,
    val previousPeriodAmount: Double,
    val deltaPercentage: Double,
    val isMoreSpent: Boolean,
    val differenceAmount: Double,
    val summaryText: String
)
