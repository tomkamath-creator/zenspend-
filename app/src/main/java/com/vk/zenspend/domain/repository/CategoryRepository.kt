package com.vk.zenspend.domain.repository

import com.vk.zenspend.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoryById(id: String): Flow<Category?>
    suspend fun insertCategory(category: Category)
    suspend fun insertCategories(categories: List<Category>)
    suspend fun deleteCategory(category: Category)
}
