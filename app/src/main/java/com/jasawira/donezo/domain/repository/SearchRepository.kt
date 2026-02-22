package com.jasawira.donezo.domain.repository

import com.jasawira.donezo.domain.model.ChecklistStatus
import com.jasawira.donezo.domain.model.GlobalSearchResult

/**
 * Repository Interface untuk Search (kombinasi card + item)
 */
interface SearchRepository {
    /**
     * Global search di card dan item
     */
    suspend fun globalSearch(query: String): GlobalSearchResult

    /**
     * Search dengan filter kategori dan status
     */
    suspend fun advancedSearch(
        query: String,
        categoryId: String?,
        status: ChecklistStatus
    ): GlobalSearchResult
}

