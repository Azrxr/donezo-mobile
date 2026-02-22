package com.jasawira.donezo.data.repository

import com.jasawira.donezo.data.local.dao.CardDao
import com.jasawira.donezo.data.local.dao.ChecklistItemDao
import com.jasawira.donezo.data.mapper.CardMapper
import com.jasawira.donezo.data.mapper.CardWithItemsMapper
import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.CardWithChecklistItems
import com.jasawira.donezo.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * IMPLEMENTATION: CardRepository
 */
class CardRepositoryImpl(
    private val cardDao: CardDao,
    private val checklistItemDao: ChecklistItemDao
) : CardRepository {

    override fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards()
            .combine(
                // Combine dengan item counts
                kotlinx.coroutines.flow.flowOf(Unit)
            ) { cards, _ ->
                // Fetch counts untuk setiap card
                cards.map { card ->
                    CardMapper.toDomain(card)
                }
            }
    }

    override suspend fun getCardWithItems(cardId: String): CardWithChecklistItems? {
        val cardWithItems = cardDao.getCardWithItems(cardId) ?: return null
        val totalCount = cardWithItems.items.size
        val completedCount = cardWithItems.items.count { it.isChecked }

        return CardWithItemsMapper.toDomain(cardWithItems, totalCount, completedCount)
    }

    override fun getCardsByCategory(categoryId: String): Flow<List<Card>> {
        return cardDao.getCardsByCategory(categoryId)
            .map { entities ->
                entities.map { CardMapper.toDomain(it) }
            }
    }

    override suspend fun addCard(card: Card): String {
        val entity = CardMapper.toEntity(card)
        cardDao.insert(entity)
        return card.id
    }

    override suspend fun updateCard(card: Card): Boolean {
        val entity = CardMapper.toEntity(card).copy(updatedAt = LocalDateTime.now())
        val result = cardDao.update(entity)
        return result > 0
    }

    override suspend fun deleteCard(cardId: String): Boolean {
        val result = cardDao.deleteById(cardId)
        return result > 0
    }

    override suspend fun updateCardPosition(cardId: String, newPosition: Int): Boolean {
        val result = cardDao.updatePosition(cardId, newPosition)
        return result > 0
    }

    override suspend fun searchCards(query: String): List<Card> {
        val entities = cardDao.searchCards(query)
        return CardMapper.toDomainList(entities)
    }

    override fun getCardProgress(cardId: String): Flow<Pair<Int, Int>> {
        return checklistItemDao.getTotalItemCount(cardId)
            .combine(checklistItemDao.getCheckedItemCount(cardId)) { total, checked ->
                total to checked
            }
    }
}