package com.jasawira.donezo.data.repository

import com.jasawira.donezo.data.local.dao.CategoryDao
import com.jasawira.donezo.data.mapper.CategoryMapper
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * IMPLEMENTATION: CategoryRepository
 */
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { entities ->
                CategoryMapper.toDomainList(entities)
            }
    }

    override suspend fun getCategoryById(id: String): Category? {
        val entity = categoryDao.getCategoryById(id)
        return entity?.let { CategoryMapper.toDomain(it) }
    }

    override suspend fun addCategory(category: Category): String {
        val entity = CategoryMapper.toEntity(category)
        categoryDao.insert(entity)
        return category.id
    }

    override suspend fun updateCategory(category: Category): Boolean {
        val entity = CategoryMapper.toEntity(category)
        val result = categoryDao.update(entity)
        return result > 0
    }

    override suspend fun deleteCategory(categoryId: String): Boolean {
        val result = categoryDao.deleteById(categoryId)
        return result > 0
    }

    override suspend fun categoryExists(id: String): Boolean {
        return categoryDao.categoryExists(id) > 0
    }
}