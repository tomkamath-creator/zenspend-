package com.vk.zenspend

import com.vk.zenspend.data.local.entity.CategoryEntity
import com.vk.zenspend.data.local.entity.DebtEntity
import com.vk.zenspend.data.local.entity.TransactionEntity
import com.vk.zenspend.data.mapper.toDomain
import com.vk.zenspend.data.mapper.toEntity
import com.vk.zenspend.domain.model.DebtType
import com.vk.zenspend.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainMappingTest {

    @Test
    fun transactionEntity_toDomainAndBack_preservesData() {
        val entity = TransactionEntity(
            id = 1L,
            title = "Matcha Green Tea",
            amount = 4.75,
            type = TransactionType.EXPENSE,
            categoryId = "mindful_dining",
            categoryName = "Mindful Dining",
            categoryIcon = "Restaurant",
            categoryColor = 0xFF48BB78L,
            timestamp = 1718000000000L,
            note = "Organic matcha",
            receiptImagePath = null,
            isExcludedFromCalmAllowance = false
        )

        val domain = entity.toDomain()
        assertEquals("Matcha Green Tea", domain.title)
        assertEquals(4.75, domain.amount, 0.001)

        val backToEntity = domain.toEntity()
        assertEquals(entity, backToEntity)
    }

    @Test
    fun categoryEntity_toDomainAndBack_preservesData() {
        val entity = CategoryEntity(
            id = "health_wellness",
            name = "Health & Wellness",
            iconName = "Spa",
            colorHex = 0xFF38B2ACL,
            type = TransactionType.EXPENSE,
            isDefault = true,
            monthlyBudgetLimit = 150.0
        )

        val domain = entity.toDomain()
        assertEquals("Health & Wellness", domain.name)
        assertEquals(150.0, domain.monthlyBudgetLimit)

        val backToEntity = domain.toEntity()
        assertEquals(entity, backToEntity)
    }

    @Test
    fun debtEntity_toDomainAndBack_preservesData() {
        val entity = DebtEntity(
            id = 2L,
            personName = "Alex",
            amount = 35.0,
            type = DebtType.LENT,
            title = "Dinner split",
            timestamp = 1718000000000L,
            dueDate = null,
            isSettled = false,
            settledDate = null,
            notes = null
        )

        val domain = entity.toDomain()
        assertEquals("Alex", domain.personName)
        assertEquals(DebtType.LENT, domain.type)

        val backToEntity = domain.toEntity()
        assertEquals(entity, backToEntity)
    }
}
