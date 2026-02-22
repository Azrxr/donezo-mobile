package com.jasawira.donezo.data.repository

import com.jasawira.donezo.data.local.dao.ChecklistItemDao
import com.jasawira.donezo.data.mapper.ChecklistItemMapper
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.domain.repository.ChecklistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * IMPLEMENTATION: ChecklistRepository
 */
class ChecklistRepositoryImpl(
    private val checklistItemDao: ChecklistItemDao
) : ChecklistRepository {

    override fun getItemsByCard(cardId: String): Flow<List<ChecklistItem>> {
        return checklistItemDao.getItemsByCard(cardId)
            .map { entities ->
                ChecklistItemMapper.toDomainList(entities)
            }
    }

    override suspend fun getPendingItemsByCard(cardId: String): List<ChecklistItem> {
        val entities = checklistItemDao.getPendingItemsByCard(cardId)
        return ChecklistItemMapper.toDomainList(entities)
    }

    override suspend fun getCompletedItemsByCard(cardId: String): List<ChecklistItem> {
        val entities = checklistItemDao.getCompletedItemsByCard(cardId)
        return ChecklistItemMapper.toDomainList(entities)
    }

    override suspend fun addItem(item: ChecklistItem): String {
        val entity = ChecklistItemMapper.toEntity(item)
        checklistItemDao.insert(entity)
        return item.id
    }

    override suspend fun updateItem(item: ChecklistItem): Boolean {
        val entity = ChecklistItemMapper.toEntity(item)
        val result = checklistItemDao.update(entity)
        return result > 0
    }

    override suspend fun updateItemCheckedStatus(itemId: String, isChecked: Boolean): Boolean {
        val result = checklistItemDao.updateCheckedStatus(itemId, isChecked)
        return result > 0
    }

    override suspend fun deleteItem(itemId: String): Boolean {
        val result = checklistItemDao.deleteById(itemId)
        return result > 0
    }

    override suspend fun updateItemPosition(itemId: String, newPosition: Int): Boolean {
        val result = checklistItemDao.updatePosition(itemId, newPosition)
        return result > 0
    }

    override suspend fun searchItems(query: String): List<ChecklistItem> {
        val entities = checklistItemDao.searchItems(query)
        return ChecklistItemMapper.toDomainList(entities)
    }

    override suspend fun getNotificationItems(): List<ChecklistItem> {
        val entities = checklistItemDao.getAllNotificationItems()
        return ChecklistItemMapper.toDomainList(entities)
    }

    override suspend fun getNotificationItemsByCard(cardId: String): List<ChecklistItem> {
        val entities = checklistItemDao.getNotificationItemsByCard(cardId)
        return ChecklistItemMapper.toDomainList(entities)
    }
}