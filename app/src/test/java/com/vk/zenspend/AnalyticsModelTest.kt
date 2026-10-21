package com.vk.zenspend

import com.vk.zenspend.domain.model.CategorySpend
import com.vk.zenspend.domain.model.SpendComparison
import com.vk.zenspend.domain.model.SpendPeriod
import com.vk.zenspend.domain.model.TrendPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsModelTest {

    @Test
    fun categorySpend_computesPercentageCorrectly() {
        val cat = CategorySpend(
            categoryId = "mindful_dining",
            categoryName = "Mindful Dining",
            colorHex = 0xFF48BB78L,
            iconName = "Restaurant",
            amount = 3500.0,
            percentage = 0.35f
        )

        assertEquals("Mindful Dining", cat.categoryName)
        assertEquals(3500.0, cat.amount, 0.001)
        assertEquals(0.35f, cat.percentage, 0.001f)
    }

    @Test
    fun spendComparison_tracksMoreSpentOrLessSpent() {
        val moreSpent = SpendComparison(
            currentPeriodAmount = 25000.0,
            previousPeriodAmount = 20000.0,
            deltaPercentage = 25.0,
            isMoreSpent = true,
            differenceAmount = 5000.0,
            summaryText = "↑ 25.0% more spent than last month"
        )
        assertTrue(moreSpent.isMoreSpent)
        assertEquals(5000.0, moreSpent.differenceAmount, 0.001)

        val lessSpent = SpendComparison(
            currentPeriodAmount = 15000.0,
            previousPeriodAmount = 20000.0,
            deltaPercentage = -25.0,
            isMoreSpent = false,
            differenceAmount = 5000.0,
            summaryText = "↓ 25.0% less spent than last month (Saved ₹5,000)"
        )
        assertFalse(lessSpent.isMoreSpent)
    }

    @Test
    fun trendPoint_holdsCoordinatesCorrectly() {
        val point = TrendPoint(
            label = "Day 15",
            value = 1200.0,
            secondaryLabel = "Oct 15"
        )
        assertEquals("Day 15", point.label)
        assertEquals(1200.0, point.value, 0.001)
    }
}
