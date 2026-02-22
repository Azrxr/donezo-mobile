package com.jasawira.donezo.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Domain Model untuk Category
 * Ini adalah pure data class tanpa Room annotation
 * Digunakan di business logic dan UI layer
 */
data class Category(
    val id: String,
    val name: String,
    val createdAt: LocalDateTime
)

/**
 * Domain Model untuk Card
 * Berisi informasi project/card checklist
 */
data class Card(
    val id: String,
    val name: String,
    val categoryId: String,
    val colorPresetId: Int,
    val position: Int = 0,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // Calculated properties
    val itemCount: Int = 0,
    val completedItemCount: Int = 0,
    val progress: Float = 0f // 0.0 - 1.0
) {
    /**
     * Helper: Get progress sebagai percentage (0-100)
     */
    fun getProgressPercentage(): Int {
        return if (itemCount == 0) 0 else ((completedItemCount.toFloat() / itemCount) * 100).toInt()
    }
}

/**
 * Domain Model untuk Checklist Item
 */
data class ChecklistItem(
    val id: String,
    val cardId: String,
    val itemName: String,
    val isChecked: Boolean = false,
    val deadline: LocalDate? = null,
    val notificationTime: LocalTime? = null,
    val notificationMinutesBefore: Int = 30,
    val isNotificationEnabled: Boolean = false,
    val position: Int = 0,
    val createdAt: LocalDateTime
)

/**
 * Domain Model untuk Card dengan list items-nya
 * Digunakan saat fetch card detail dengan items
 */
data class CardWithChecklistItems(
    val card: Card,
    val items: List<ChecklistItem> = emptyList()
)

/**
 * Model untuk filter
 */
data class FilterOptions(
    val categoryId: String? = null,
    val status: ChecklistStatus = ChecklistStatus.ALL,
    val searchQuery: String = ""
)

enum class ChecklistStatus {
    ALL, PENDING, COMPLETED
}

/**
 * Model untuk search result
 */
data class GlobalSearchResult(
    val cards: List<Card>,
    val items: List<ChecklistItem>
)

