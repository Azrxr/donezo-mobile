package com.jasawira.donezo.domain.repository

import com.jasawira.donezo.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository Interface untuk Category
 * Mendefinisikan contract untuk operasi kategori
 *
 * Implementasi: CategoryRepositoryImpl
 */
interface CategoryRepository {
    /**
     * Mendapatkan semua kategori
     * Return: Flow untuk reactive updates
     */
    fun getAllCategories(): Flow<List<Category>>

    /**
     * Mendapatkan kategori by id
     */
    suspend fun getCategoryById(id: String): Category?

    /**
     * Menambah kategori baru
     */
    suspend fun addCategory(category: Category): String

    /**
     * Update kategori
     */
    suspend fun updateCategory(category: Category): Boolean

    /**
     * Delete kategori
     */
    suspend fun deleteCategory(categoryId: String): Boolean

    /**
     * Check jika kategori ada
     */
    suspend fun categoryExists(id: String): Boolean
}