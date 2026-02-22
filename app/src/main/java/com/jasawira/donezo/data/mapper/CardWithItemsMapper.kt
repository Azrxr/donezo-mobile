package com.jasawira.donezo.data.mapper

import com.jasawira.donezo.data.local.entity.CardWithItems
import com.jasawira.donezo.domain.model.CardWithChecklistItems


/**
 * Mapper untuk CardWithItems
 */
object CardWithItemsMapper {
    fun toDomain(
        entity: CardWithItems,
        itemCount: Int = 0,
        completedItemCount: Int = 0
    ): CardWithChecklistItems {
        return CardWithChecklistItems(
            card = CardMapper.toDomain(entity.card, itemCount, completedItemCount),
            items = ChecklistItemMapper.toDomainList(entity.items)
        )
    }
}
