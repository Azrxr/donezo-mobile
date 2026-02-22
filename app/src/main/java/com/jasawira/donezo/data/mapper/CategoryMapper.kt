package com.jasawira.donezo.data.mapper

import com.jasawira.donezo.data.local.entity.CategoryEntity
import com.jasawira.donezo.domain.model.Category


/**
 * Mapper untuk Category
 */
object CategoryMapper {
    fun toDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            id = domain.id,
            name = domain.name,
            createdAt = domain.createdAt
        )
    }

    fun toDomainList(entities: List<CategoryEntity>): List<Category> {
        return entities.map { toDomain(it) }
    }
}