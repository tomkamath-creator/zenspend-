package com.vk.zenspend.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vk.zenspend.data.local.ZenSpendDatabase
import com.vk.zenspend.data.local.dao.BudgetDao
import com.vk.zenspend.data.local.dao.CategoryDao
import com.vk.zenspend.data.local.dao.DebtDao
import com.vk.zenspend.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideZenSpendDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>
    ): ZenSpendDatabase {
        return Room.databaseBuilder(
            context,
            ZenSpendDatabase::class.java,
            ZenSpendDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Prepopulate default mindful categories
                    CoroutineScope(Dispatchers.IO).launch {
                        categoryDaoProvider.get().insertCategories(ZenSpendDatabase.DEFAULT_CATEGORIES)
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideTransactionDao(database: ZenSpendDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideCategoryDao(database: ZenSpendDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(database: ZenSpendDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    fun provideDebtDao(database: ZenSpendDatabase): DebtDao {
        return database.debtDao()
    }

    @Provides
    fun provideEmiDao(database: ZenSpendDatabase): com.vk.zenspend.data.local.dao.EmiDao {
        return database.emiDao()
    }
}
