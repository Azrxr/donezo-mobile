package com.jasawira.donezo.data.repository

import com.jasawira.donezo.data.local.dao.CardDao
import com.jasawira.donezo.data.local.dao.ChecklistItemDao
import com.jasawira.donezo.data.mapper.CardMapper
import com.jasawira.donezo.data.mapper.ChecklistItemMapper
import com.jasawira.donezo.domain.model.ChecklistStatus
import com.jasawira.donezo.domain.model.GlobalSearchResult
import com.jasawira.donezo.domain.repository.SearchRepository


/**
 * IMPLEMENTATION: SearchRepository
 */
class SearchRepositoryImpl(
    private val cardDao: CardDao,
    private val checklistItemDao: ChecklistItemDao
) : SearchRepository {

    override suspend fun globalSearch(query: String): GlobalSearchResult {
        val cardEntities = cardDao.searchCards(query)
        val itemEntities = checklistItemDao.searchItems(query)

        return GlobalSearchResult(
            cards = CardMapper.toDomainList(cardEntities),
            items = ChecklistItemMapper.toDomainList(itemEntities)
        )
    }

    override suspend fun advancedSearch(
        query: String,
        categoryId: String?,
        status: ChecklistStatus
    ): GlobalSearchResult {
        // Search cards
        var cardEntities = cardDao.searchCards(query)
        if (categoryId != null) {
            cardEntities = cardEntities.filter { it.categoryId == categoryId }
        }

        // Search items
        var itemEntities = checklistItemDao.searchItems(query)
        itemEntities = when (status) {
            ChecklistStatus.COMPLETED -> itemEntities.filter { it.isChecked }
            ChecklistStatus.PENDING -> itemEntities.filter { !it.isChecked }
            ChecklistStatus.ALL -> itemEntities
        }

        return GlobalSearchResult(
            cards = CardMapper.toDomainList(cardEntities),
            items = ChecklistItemMapper.toDomainList(itemEntities)
        )
    }
}