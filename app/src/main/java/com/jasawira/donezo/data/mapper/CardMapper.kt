package com.jasawira.donezo.data.mapper

import com.jasawira.donezo.data.local.entity.CardEntity
import com.jasawira.donezo.domain.model.Card


/**
 * Mapper untuk Card
 */
object CardMapper {
    fun toDomain(
        entity: CardEntity,
        itemCount: Int = 0,
        completedItemCount: Int = 0
    ): Card {
        val progress = if (itemCount == 0) 0f else completedItemCount.toFloat() / itemCount
        return Card(
            id = entity.id,
            name = entity.name,
            categoryId = entity.categoryId,
            colorPresetId = entity.colorPresetId,
            position = entity.position,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            itemCount = itemCount,
            completedItemCount = completedItemCount,
            progress = progress
        )
    }

    fun toEntity(domain: Card): CardEntity {
        return CardEntity(
            id = domain.id,
            name = domain.name,
            categoryId = domain.categoryId,
            colorPresetId = domain.colorPresetId,
            position = domain.position,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomainList(
        entities: List<CardEntity>,
        itemCountsMap: Map<String, Pair<Int, Int>> = emptyMap()
    ): List<Card> {
        return entities.map { entity ->
            val (itemCount, completedCount) = itemCountsMap[entity.id] ?: (0 to 0)
            toDomain(entity, itemCount, completedCount)
        }
    }
}