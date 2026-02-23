package com.jasawira.donezo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jasawira.donezo.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk CHECKLIST ITEM
 */
@Dao
interface ChecklistItemDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChecklistItemEntity): Long

    // READ
    @Query("SELECT * FROM checklist_items WHERE cardId = :cardId ORDER BY position ASC")
    fun getItemsByCard(cardId: String): Flow<List<ChecklistItemEntity>>

    // Observe all items count (untuk trigger refresh di Home saat items berubah)
    @Query("SELECT COUNT(*) FROM checklist_items")
    fun observeAllItemsCount(): Flow<Int>

    @Query("SELECT * FROM checklist_items WHERE id = :id")
    suspend fun getItemById(id: String): ChecklistItemEntity?

    // READ dengan filter
    @Query("""
        SELECT * FROM checklist_items 
        WHERE cardId = :cardId AND isChecked = 0 
        ORDER BY position ASC
    """)
    suspend fun getPendingItemsByCard(cardId: String): List<ChecklistItemEntity>

    @Query("""
        SELECT * FROM checklist_items 
        WHERE cardId = :cardId AND isChecked = 1 
        ORDER BY position ASC
    """)
    suspend fun getCompletedItemsByCard(cardId: String): List<ChecklistItemEntity>

    // UPDATE
    @Update
    suspend fun update(item: ChecklistItemEntity): Int

    @Query("UPDATE checklist_items SET isChecked = :isChecked WHERE id = :itemId")
    suspend fun updateCheckedStatus(itemId: String, isChecked: Boolean): Int

    @Query("UPDATE checklist_items SET position = :position WHERE id = :itemId")
    suspend fun updatePosition(itemId: String, position: Int): Int

    // DELETE
    @Delete
    suspend fun delete(item: ChecklistItemEntity): Int

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM checklist_items WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: String): Int

    // Notification queries
    @Query("""
        SELECT * FROM checklist_items 
        WHERE isNotificationEnabled = 1 
        AND deadline IS NOT NULL
        AND notificationTime IS NOT NULL
        ORDER BY deadline ASC, notificationTime ASC
    """)
    suspend fun getAllNotificationItems(): List<ChecklistItemEntity>

    @Query("""
        SELECT * FROM checklist_items 
        WHERE cardId = :cardId 
        AND isNotificationEnabled = 1 
        AND deadline IS NOT NULL
        AND notificationTime IS NOT NULL
    """)
    suspend fun getNotificationItemsByCard(cardId: String): List<ChecklistItemEntity>

    // Search
    @Query("""
        SELECT * FROM checklist_items 
        WHERE itemName LIKE '%' || :query || '%' 
        ORDER BY position ASC
    """)
    suspend fun searchItems(query: String): List<ChecklistItemEntity>

    // Helper: Get max position dalam card untuk urutan baru
    @Query("SELECT COALESCE(MAX(position), -1) FROM checklist_items WHERE cardId = :cardId")
    suspend fun getMaxPositionInCard(cardId: String): Int

    // Helper: Get max position untuk card
    @Query("SELECT COALESCE(MAX(position), -1) FROM cards")
    suspend fun getMaxCardPosition(): Int

    // Count checked items per card
    @Query("SELECT COUNT(*) FROM checklist_items WHERE cardId = :cardId AND isChecked = 1")
    fun getCheckedItemCount(cardId: String): Flow<Int>

    // Count total items per card
    @Query("SELECT COUNT(*) FROM checklist_items WHERE cardId = :cardId")
    fun getTotalItemCount(cardId: String): Flow<Int>

    // Sync versions for repository mapping
    @Query("SELECT COUNT(*) FROM checklist_items WHERE cardId = :cardId AND isChecked = 1")
    suspend fun getCheckedItemCountSync(cardId: String): Int

    @Query("SELECT COUNT(*) FROM checklist_items WHERE cardId = :cardId")
    suspend fun getTotalItemCountSync(cardId: String): Int
}