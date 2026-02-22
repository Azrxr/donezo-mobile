package com.jasawira.donezo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.model.FilterOptions
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.domain.repository.SearchRepository
import com.jasawira.donezo.presentation.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel
 * Mengelola state untuk Home Screen
 * Fitur: Fetch & display cards, search, filter, CRUD operations
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val categoryRepository: CategoryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _filterOptions = MutableStateFlow(FilterOptions())
    private val _searchQuery = MutableStateFlow("")

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                combine(
                    cardRepository.getAllCards(),
                    categoryRepository.getAllCategories(),
                    _searchQuery,
                    _filterOptions
                ) { cards, categories, query, filters ->
                    applyFiltersAndSearch(cards, query, filters) to categories
                }.collect { (filteredCards, categories) ->
                    _uiState.value = HomeUiState.Success(
                        cards = filteredCards,
                        categories = categories,
                        filteredCards = filteredCards,
                        filterOptions = _filterOptions.value,
                        searchQuery = _searchQuery.value
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun applyFiltersAndSearch(
        cards: List<Card>,
        query: String,
        filters: FilterOptions
    ): List<Card> {
        var filtered = cards

        // Filter by category
        if (filters.categoryId != null) {
            filtered = filtered.filter { it.categoryId == filters.categoryId }
        }

        // Search
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        return filtered
    }

    fun searchCards(query: String) {
        _searchQuery.value = query
    }

    fun filterByCategory(categoryId: String?) {
        _filterOptions.value = _filterOptions.value.copy(categoryId = categoryId)
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                cardRepository.deleteCard(cardId)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Gagal menghapus card: ${e.message}")
            }
        }
    }

    fun clearFilter() {
        _filterOptions.value = FilterOptions()
        _searchQuery.value = ""
    }

    fun refreshCards() {
        loadHomeData()
    }
}

