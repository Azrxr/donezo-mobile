package com.jasawira.donezo.domain.repository

import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.CardWithChecklistItems
import kotlinx.coroutines.flow.Flow

/**
 * Repository Interface untuk Card
 */
interface CardRepository {
    /**
     * Mendapatkan semua card
     */
    fun getAllCards(): Flow<List<Card>>

    /**
     * Mendapatkan card by id dengan items-nya
     */
    suspend fun getCardWithItems(cardId: String): CardWithChecklistItems?

    /**
     * Mendapatkan card by kategori
     */
    fun getCardsByCategory(categoryId: String): Flow<List<Card>>

    /**
     * Menambah card baru
     */
    suspend fun addCard(card: Card): String

    /**
     * Update card
     */
    suspend fun updateCard(card: Card): Boolean

    /**
     * Delete card (items akan otomatis delete cascade)
     */
    suspend fun deleteCard(cardId: String): Boolean

    /**
     * Update posisi card untuk reorder
     */
    suspend fun updateCardPosition(cardId: String, newPosition: Int): Boolean

    /**
     * Search card by name
     */
    suspend fun searchCards(query: String): List<Card>

    /**
     * Get progress/stats untuk card
     */
    fun getCardProgress(cardId: String): Flow<Pair<Int, Int>> // Pair<total, completed>
}