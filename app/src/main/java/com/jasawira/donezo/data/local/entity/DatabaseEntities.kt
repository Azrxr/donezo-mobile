package com.jasawira.donezo.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * TAHAP 1: CATEGORY ENTITY
 * Menyimpan kategori untuk card
 */
@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * TAHAP 2: CARD ENTITY
 * Menyimpan kartu project dengan kategori dan warna
 */
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["position"])
    ]
)
data class CardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val categoryId: String,
    val colorPresetId: Int, // 0-9 untuk 10 preset warna
    val position: Int = 0, // Untuk urutan drag-drop
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * TAHAP 3: CHECKLIST ITEM ENTITY
 * Menyimpan item checklist dalam sebuah card
 */
@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cardId"]),
        Index(value = ["position"])
    ]
)
data class ChecklistItemEntity(
    @PrimaryKey val id: String,
    val cardId: String,
    val itemName: String,
    val isChecked: Boolean = false,
    val deadline: LocalDate? = null,
    val notificationTime: LocalTime? = null,
    val notificationMinutesBefore: Int = 30, // Default 30 menit sebelum deadline
    val isNotificationEnabled: Boolean = false,
    val position: Int = 0, // Untuk urutan drag-drop
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Helper data class untuk query hasil
 * Menggabungkan Card dengan list items-nya
 */
data class CardWithItems(
    @Embedded val card: CardEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val items: List<ChecklistItemEntity>
)

/**
 * Helper untuk search result
 */
data class SearchResult(
    val type: SearchResultType,
    val card: CardEntity?,
    val item: ChecklistItemEntity?
)

enum class SearchResultType {
    CARD, ITEM
}
