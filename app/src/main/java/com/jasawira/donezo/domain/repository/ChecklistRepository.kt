package com.jasawira.donezo.domain.repository

import com.jasawira.donezo.domain.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository Interface untuk ChecklistItem
 */
interface ChecklistRepository {
    /**
     * Mendapatkan items by card id
     */
    fun getItemsByCard(cardId: String): Flow<List<ChecklistItem>>

    /**
     * Mendapatkan pending items only
     */
    suspend fun getPendingItemsByCard(cardId: String): List<ChecklistItem>

    /**
     * Mendapatkan completed items only
     */
    suspend fun getCompletedItemsByCard(cardId: String): List<ChecklistItem>

    /**
     * Menambah item baru
     */
    suspend fun addItem(item: ChecklistItem): String

    /**
     * Update item
     */
    suspend fun updateItem(item: ChecklistItem): Boolean

    /**
     * Update status checked
     */
    suspend fun updateItemCheckedStatus(itemId: String, isChecked: Boolean): Boolean

    /**
     * Delete item
     */
    suspend fun deleteItem(itemId: String): Boolean

    /**
     * Update posisi item untuk reorder
     */
    suspend fun updateItemPosition(itemId: String, newPosition: Int): Boolean

    /**
     * Search items by name
     */
    suspend fun searchItems(query: String): List<ChecklistItem>

    /**
     * Mendapatkan semua items dengan notifikasi
     */
    suspend fun getNotificationItems(): List<ChecklistItem>

    /**
     * Mendapatkan items dengan notifikasi untuk card tertentu
     */
    suspend fun getNotificationItemsByCard(cardId: String): List<ChecklistItem>

    /**
     * Observe total item count (trigger refresh saat items berubah)
     */
    fun observeAllItemsCount(): Flow<Int>
}