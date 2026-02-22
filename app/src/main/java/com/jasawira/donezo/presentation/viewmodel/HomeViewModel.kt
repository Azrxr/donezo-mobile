package com.jasawira.donezo.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.model.ChecklistStatus
import com.jasawira.donezo.domain.model.FilterOptions
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.domain.repository.ChecklistRepository
import com.jasawira.donezo.domain.repository.SearchRepository
import com.jasawira.donezo.presentation.uistate.HomeUiEvent
import com.jasawira.donezo.presentation.uistate.HomeUiState
import com.jasawira.donezo.presentation.uistate.CardPreview
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.utils.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel
 * Mengelola state untuk Home Screen
 *
 * Fitur:
 * - Fetch dan display semua cards
 * - Search cards & items
 * - Filter by category & status
 * - CRUD operations untuk cards
 * - Drag-drop reorder cards
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val categoryRepository: CategoryRepository,
    private val checklistRepository: ChecklistRepository,
    private val searchRepository: SearchRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    // USER PREFERENCES - lazy initialization
    private val userPreferences: UserPreferencesManager by lazy {
        UserPreferencesManager(context)
    }

    // UI STATE
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // USERNAME STATE
    private val _userName = MutableStateFlow("Sobat")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // SNACKBAR EVENT
    private val _snackbarEvent = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    // FILTER OPTIONS
    private val _filterOptions = MutableStateFlow(FilterOptions())
    private val _searchQuery = MutableStateFlow("")


    // ALL DATA
    private val _allCards = cardRepository.getAllCards()
    private val _allCategories = categoryRepository.getAllCategories()

    init {
        loadUserNameFromPreferences()
        loadHomeData()
    }

    /**
     * Load initial data untuk home screen
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                // Combine all cards dan categories
                combine(
                    _allCards,
                    _allCategories,
                    _searchQuery,
                    _filterOptions
                ) { cards, categories, query, filters ->
                    applyFiltersAndSearch(cards, categories, query, filters)
                }.collect { (filteredCards, categories) ->
                    _uiState.value = HomeUiState.Success(
                        cards = filteredCards,
                        categories = categories,
                        filteredCards = filteredCards,
                        filterOptions = _filterOptions.value,
                        searchQuery = _searchQuery.value,
                        isDarkMode = false // TODO: Get from settings
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Apply search dan filter ke cards
     */
    private suspend fun applyFiltersAndSearch(
        allCards: List<Card>,
        categories: List<Category>,
        query: String,
        filters: FilterOptions
    ): Pair<List<Card>, List<Category>> {
        var filtered = allCards

        // Filter by category
        if (filters.categoryId != null) {
            filtered = filtered.filter { it.categoryId == filters.categoryId }
        }

        // Filter by status (belum ada status di card level, ini untuk future)
        // Currently implemented at item level in detail screen

        // Search by name
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        return filtered to categories
    }

    /**
     * Handle UI Events
     */
    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.SearchCards -> searchCards(event.query)
            is HomeUiEvent.FilterByCategory -> filterByCategory(event.categoryId)
            is HomeUiEvent.FilterByStatus -> filterByStatus(event.status)
            is HomeUiEvent.DeleteCard -> deleteCard(event.cardId)
            is HomeUiEvent.ReorderCard -> reorderCard(event.cardId, event.newPosition)
            is HomeUiEvent.UpdateCardPosition -> updateCardPositions(event.fromPosition, event.toPosition)
            is HomeUiEvent.ClearFilter -> clearFilter()
            is HomeUiEvent.RefreshCards -> loadHomeData()
        }
    }

    /**
     * Search cards by query
     */
    private fun searchCards(query: String) {
        _searchQuery.value = query
    }

    /**
     * Filter by kategori
     */
    private fun filterByCategory(categoryId: String?) {
        _filterOptions.value = _filterOptions.value.copy(categoryId = categoryId)
    }

    /**
     * Filter by status (Done, Pending, All)
     */
    private fun filterByStatus(status: String) {
        val checklistStatus = when (status) {
            "COMPLETED" -> ChecklistStatus.COMPLETED
            "PENDING" -> ChecklistStatus.PENDING
            else -> ChecklistStatus.ALL
        }
        _filterOptions.value = _filterOptions.value.copy(status = checklistStatus)
    }

    /**
     * Delete card dengan konfirmasi
     */
    private fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                val result = cardRepository.deleteCard(cardId)
                if (result) {
                    _snackbarEvent.emit(
                        SnackbarEvent.Success("Card berhasil dihapus")
                    )
                } else {
                    _snackbarEvent.emit(
                        SnackbarEvent.Error("Gagal menghapus card")
                    )
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(
                    SnackbarEvent.Error("Error: ${e.message}")
                )
            }
        }
    }

    /**
     * Reorder card ke posisi baru
     */
    private fun reorderCard(cardId: String, newPosition: Int) {
        viewModelScope.launch {
            try {
                cardRepository.updateCardPosition(cardId, newPosition)
            } catch (e: Exception) {
                _snackbarEvent.emit(
                    SnackbarEvent.Error("Gagal mengubah urutan card")
                )
            }
        }
    }

    /**
     * Update posisi card saat drag-drop
     */
    private fun updateCardPositions(fromPosition: Int, toPosition: Int) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val cards = currentState.cards.toMutableList()
            if (fromPosition < cards.size && toPosition < cards.size) {
                val movedCard = cards.removeAt(fromPosition)
                cards.add(toPosition, movedCard)

                // Update database dengan posisi baru
                viewModelScope.launch {
                    try {
                        cards.forEachIndexed { index, card ->
                            cardRepository.updateCardPosition(card.id, index)
                        }
                    } catch (e: Exception) {
                        _snackbarEvent.emit(
                            SnackbarEvent.Error("Gagal mengubah urutan")
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear semua filter
     */
    private fun clearFilter() {
        _searchQuery.value = ""
        _filterOptions.value = FilterOptions()
    }

    /**
     * Add new card dengan random color preset
     */
    fun addCard(
        name: String,
        categoryId: String,
        colorPresetId: Int = kotlin.random.Random.nextInt(0, 10) // Random color 0-9
    ) {
        if (name.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama card tidak boleh kosong"))
            }
            return
        }

        if (categoryId.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Pilih kategori terlebih dahulu"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val card = Card(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    categoryId = categoryId,
                    colorPresetId = colorPresetId,
                    position = 0,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                cardRepository.addCard(card)
                _snackbarEvent.emit(SnackbarEvent.Success("Card berhasil dibuat"))
            } catch (e: Exception) {
                e.printStackTrace()
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal membuat card: ${e.message}"))
            }
        }
    }

    /**
     * Update user name
     */
    fun updateUserName(newName: String) {
        _userName.value = newName
        userPreferences.setUsername(newName)
    }

    /**
     * Load username dari preferences saat init
     */
    fun loadUserNameFromPreferences() {
        _userName.value = userPreferences.getUsername()
    }

    /**
     * Get color preset
     */
    fun getColorPreset(): Int {
        return userPreferences.getColorPreset()
    }

    /**
     * Set color preset
     */
    fun setColorPreset(colorPresetId: Int) {
        userPreferences.setColorPreset(colorPresetId)
    }

    /**
     * Update card (name, color, category)
     */
    fun updateCard(
        cardId: String,
        name: String,
        categoryId: String,
        colorPresetId: Int
    ) {
        if (name.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama card tidak boleh kosong"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    val card = currentState.cards.find { it.id == cardId }
                    if (card != null) {
                        val updatedCard = card.copy(
                            name = name,
                            categoryId = categoryId,
                            colorPresetId = colorPresetId
                        )
                        cardRepository.updateCard(updatedCard)
                        _snackbarEvent.emit(SnackbarEvent.Success("Card berhasil diupdate"))
                    }
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update card"))
            }
        }
    }

    /**
     * Get current state untuk debugging
     */
    fun getCurrentCards(): List<Card> {
        return (uiState.value as? HomeUiState.Success)?.cards ?: emptyList()
    }

    fun getCurrentCategories(): List<Category> {
        return (uiState.value as? HomeUiState.Success)?.categories ?: emptyList()
    }
}

