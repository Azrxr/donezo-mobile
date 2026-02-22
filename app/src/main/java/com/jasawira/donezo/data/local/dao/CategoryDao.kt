package com.jasawira.donezo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jasawira.donezo.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow


/**
 * DAO untuk CATEGORY
 */
@Dao
interface CategoryDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    // READ
    @Query("SELECT * FROM categories ORDER BY createdAt DESC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    // UPDATE
    @Update
    suspend fun update(category: CategoryEntity): Int

    // DELETE
    @Delete
    suspend fun delete(category: CategoryEntity): Int

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: String): Int

    // Helper: Check if category exists
    @Query("SELECT COUNT(*) FROM categories WHERE id = :id")
    suspend fun categoryExists(id: String): Int
}