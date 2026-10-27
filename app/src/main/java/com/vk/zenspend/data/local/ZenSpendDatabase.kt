package com.vk.zenspend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vk.zenspend.data.local.converter.Converters
import com.vk.zenspend.data.local.dao.BudgetDao
import com.vk.zenspend.data.local.dao.CategoryDao
import com.vk.zenspend.data.local.dao.DebtDao
import com.vk.zenspend.data.local.dao.TransactionDao
import com.vk.zenspend.data.local.entity.BudgetEntity
import com.vk.zenspend.data.local.entity.CategoryEntity
import com.vk.zenspend.data.local.entity.DebtEntity
import com.vk.zenspend.data.local.entity.TransactionEntity
import com.vk.zenspend.data.local.dao.EmiDao
import com.vk.zenspend.data.local.entity.EmiEntity
import com.vk.zenspend.domain.model.TransactionType

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        DebtEntity::class,
        EmiEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ZenSpendDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao
    abstract fun emiDao(): EmiDao

    companion object {
        const val DATABASE_NAME = "zenspend.db"

        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(
                id = "mindful_dining",
                name = "Food & Dining",
                iconName = "Restaurant",
                colorHex = 0xFF48BB78L,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "serene_living",
                name = "Housing & Rent",
                iconName = "Home",
                colorHex = 0xFFED8936L,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "health_wellness",
                name = "Health & Fitness",
                iconName = "Spa",
                colorHex = 0xFF38B2ACL,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "daily_transit",
                name = "Travel & Transport",
                iconName = "DirectionsBike",
                colorHex = 0xFF4299E1L,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "focus_growth",
                name = "Education & Growth",
                iconName = "MenuBook",
                colorHex = 0xFF9F7AEAL,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "coffee_rituals",
                name = "Coffee & Snacks",
                iconName = "LocalCafe",
                colorHex = 0xFFD69E2EL,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "leisure_play",
                name = "Entertainment & Leisure",
                iconName = "Palette",
                colorHex = 0xFFF56565L,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "calm_essentials",
                name = "Bills & Utilities",
                iconName = "ShoppingBag",
                colorHex = 0xFF718096L,
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            CategoryEntity(
                id = "zen_stream",
                name = "Salary & Income",
                iconName = "Payments",
                colorHex = 0xFF2ECC71L,
                type = TransactionType.INCOME,
                isDefault = true
            ),
            CategoryEntity(
                id = "side_ventures",
                name = "Side Ventures",
                iconName = "Savings",
                colorHex = 0xFFECC94BL,
                type = TransactionType.INCOME,
                isDefault = true
            )
        )
    }
}
