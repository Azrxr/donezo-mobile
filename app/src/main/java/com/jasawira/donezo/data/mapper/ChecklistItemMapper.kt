package com.jasawira.donezo.data.mapper

import com.jasawira.donezo.data.local.entity.ChecklistItemEntity
import com.jasawira.donezo.domain.model.ChecklistItem

/**
 * Mapper untuk ChecklistItem
 */
object ChecklistItemMapper {
    fun toDomain(entity: ChecklistItemEntity): ChecklistItem {
        return ChecklistItem(
            id = entity.id,
            cardId = entity.cardId,
            itemName = entity.itemName,
            isChecked = entity.isChecked,
            deadline = entity.deadline,
            notificationTime = entity.notificationTime,
            notificationMinutesBefore = entity.notificationMinutesBefore,
            isNotificationEnabled = entity.isNotificationEnabled,
            position = entity.position,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: ChecklistItem): ChecklistItemEntity {
        return ChecklistItemEntity(
            id = domain.id,
            cardId = domain.cardId,
            itemName = domain.itemName,
            isChecked = domain.isChecked,
            deadline = domain.deadline,
            notificationTime = domain.notificationTime,
            notificationMinutesBefore = domain.notificationMinutesBefore,
            isNotificationEnabled = domain.isNotificationEnabled,
            position = domain.position,
            createdAt = domain.createdAt
        )
    }

    fun toDomainList(entities: List<ChecklistItemEntity>): List<ChecklistItem> {
        return entities.map { toDomain(it) }
    }
}