package com.jasawira.donezo.presentation.uistate

import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.CardWithChecklistItems
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.domain.model.FilterOptions

/**
 * Card Preview untuk Home Screen
 * Berisi card data + preview items (max 3)
 */
data class CardPreview(
    val card: Card,
    val previewItems: List<ChecklistItem> = emptyList()
)

/**
 * UI STATE untuk Home Screen
 * Menggunakan sealed class untuk type-safe state handling
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val cards: List<Card> = emptyList(),
        val cardPreviews: List<CardPreview> = emptyList(),
        val categories: List<Category> = emptyList(),
        val filteredCards: List<Card> = emptyList(),
        val filterOptions: FilterOptions = FilterOptions(),
        val searchQuery: String = "",
        val isDarkMode: Boolean = false,
        // Edit Mode State
        val isEditMode: Boolean = false,
        val selectedCardIds: Set<String> = emptySet()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * UI EVENT untuk Home Screen
 * Digunakan untuk handle user interactions
 */
sealed class HomeUiEvent {
    data class SearchCards(val query: String) : HomeUiEvent()
    data class FilterByCategory(val categoryId: String?) : HomeUiEvent()
    data class FilterByStatus(val status: String) : HomeUiEvent()
    data class DeleteCard(val cardId: String) : HomeUiEvent()
    data class ReorderCard(val cardId: String, val newPosition: Int) : HomeUiEvent()
    data class UpdateCardPosition(val fromPosition: Int, val toPosition: Int) : HomeUiEvent()
    object ClearFilter : HomeUiEvent()
    object RefreshCards : HomeUiEvent()
    // Edit Mode Events
    object EnterEditMode : HomeUiEvent()
    object ExitEditMode : HomeUiEvent()
    data class ToggleCardSelection(val cardId: String) : HomeUiEvent()
    object DeleteSelectedCards : HomeUiEvent()
    object SelectAllCards : HomeUiEvent()
    object DeselectAllCards : HomeUiEvent()
}

/**
 * UI STATE untuk Card Detail Screen
 */
sealed class CardDetailUiState {
    object Loading : CardDetailUiState()
    data class Success(
        val cardWithItems: CardWithChecklistItems? = null,
        val isEditingCard: Boolean = false,
        val editingItemId: String? = null,
        val isDarkMode: Boolean = false
    ) : CardDetailUiState()
    data class Error(val message: String) : CardDetailUiState()
}

/**
 * UI EVENT untuk Card Detail Screen
 */
sealed class CardDetailUiEvent {
    data class UpdateCardName(val cardId: String, val newName: String) : CardDetailUiEvent()
    data class UpdateCardColor(val cardId: String, val colorPresetId: Int) : CardDetailUiEvent()
    data class UpdateCardCategory(val cardId: String, val newCategoryId: String) : CardDetailUiEvent()
    data class ChecklistItemStatusChanged(val itemId: String, val isChecked: Boolean) : CardDetailUiEvent()
    data class DeleteItem(val itemId: String) : CardDetailUiEvent()
    data class ReorderItem(val itemId: String, val newPosition: Int) : CardDetailUiEvent()
    data class UpdateItemPosition(val fromPosition: Int, val toPosition: Int) : CardDetailUiEvent()
    data class AddItem(val item: ChecklistItem) : CardDetailUiEvent()
    data class UpdateItem(val item: ChecklistItem) : CardDetailUiEvent()
    object DeleteCard : CardDetailUiEvent()
    object RefreshCardDetail : CardDetailUiEvent()
}

/**
 * UI STATE untuk Category Management Screen
 */
sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(
        val categories: List<Category> = emptyList(),
        val isAddingCategory: Boolean = false,
        val editingCategoryId: String? = null
    ) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

/**
 * UI EVENT untuk Category Management
 */
sealed class CategoryUiEvent {
    data class AddCategory(val categoryName: String) : CategoryUiEvent()
    data class UpdateCategory(val categoryId: String, val newName: String) : CategoryUiEvent()
    data class DeleteCategory(val categoryId: String) : CategoryUiEvent()
    object RefreshCategories : CategoryUiEvent()
}

/**
 * UI STATE untuk Add Item Bottom Sheet
 */
sealed class AddItemUiState {
    object Idle : AddItemUiState()
    object Loading : AddItemUiState()
    data class Success(val itemId: String) : AddItemUiState()
    data class Error(val message: String) : AddItemUiState()
}

/**
 * UI STATE untuk Add Card Bottom Sheet
 */
sealed class AddCardUiState {
    object Idle : AddCardUiState()
    object Loading : AddCardUiState()
    data class Success(val cardId: String) : AddCardUiState()
    data class Error(val message: String) : AddCardUiState()
}

/**
 * UI STATE untuk Edit Card Bottom Sheet
 */
sealed class EditCardUiState {
    object Idle : EditCardUiState()
    object Loading : EditCardUiState()
    data class Success(val cardId: String) : EditCardUiState()
    data class Error(val message: String) : EditCardUiState()
}

/**
 * UI STATE untuk Settings Screen (untuk dark mode, dll)
 */
data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isSoundEnabled: Boolean = true
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

/**
 * Snackbar Event untuk menampilkan feedback ke user
 */
sealed class SnackbarEvent {
    data class Success(val message: String) : SnackbarEvent()
    data class Error(val message: String) : SnackbarEvent()
    data class Info(val message: String) : SnackbarEvent()
    data class Undo(val message: String, val onUndo: () -> Unit) : SnackbarEvent()
}

/**
 * Dialog Event untuk menampilkan konfirmasi
 */
sealed class DialogEvent {
    data class ConfirmDelete(
        val title: String,
        val message: String,
        val onConfirm: () -> Unit
    ) : DialogEvent()
    data class Info(val title: String, val message: String) : DialogEvent()
}

