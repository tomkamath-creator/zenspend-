package com.vk.zenspend.data.repository

import com.vk.zenspend.data.local.dao.CategoryDao
import com.vk.zenspend.data.mapper.toDomain
import com.vk.zenspend.data.mapper.toEntity
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCategoryById(id: String): Flow<Category?> {
        return categoryDao.getCategoryById(id).map { it?.toDomain() }
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insertCategories(categories.map { it.toEntity() })
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }
}
