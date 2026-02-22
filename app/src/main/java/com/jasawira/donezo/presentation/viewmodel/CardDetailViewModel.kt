package com.jasawira.donezo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.CardWithChecklistItems
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.ChecklistRepository
import com.jasawira.donezo.presentation.uistate.CardDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CardDetailViewModel
 * Mengelola state untuk Card Detail Screen
 * Fitur: Load card dengan items, CRUD items, reorder, notifications
 */
@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val checklistRepository: ChecklistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CardDetailUiState>(CardDetailUiState.Loading)
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    fun loadCardWithItems(cardId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = CardDetailUiState.Loading
                val cardWithItems = cardRepository.getCardWithItems(cardId)
                _uiState.value = CardDetailUiState.Success(cardWithItems = cardWithItems)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal load card: ${e.message}")
            }
        }
    }

    fun addItem(item: ChecklistItem) {
        viewModelScope.launch {
            try {
                checklistRepository.addItem(item)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal menambah item: ${e.message}")
            }
        }
    }

    fun updateItem(item: ChecklistItem) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItem(item)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal update item: ${e.message}")
            }
        }
    }

    fun updateItemCheckedStatus(itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItemCheckedStatus(itemId, isChecked)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal update status: ${e.message}")
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                checklistRepository.deleteItem(itemId)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal hapus item: ${e.message}")
            }
        }
    }

    fun reorderItem(itemId: String, newPosition: Int) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItemPosition(itemId, newPosition)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal reorder item: ${e.message}")
            }
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                cardRepository.deleteCard(cardId)
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal hapus card: ${e.message}")
            }
        }
    }

    fun updateCardName(cardId: String, newName: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(name = newName)
                    cardRepository.updateCard(updatedCard)
                }
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal update nama: ${e.message}")
            }
        }
    }

    fun updateCardColor(cardId: String, colorPresetId: Int) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(colorPresetId = colorPresetId)
                    cardRepository.updateCard(updatedCard)
                }
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error("Gagal update warna: ${e.message}")
            }
        }
    }
}

