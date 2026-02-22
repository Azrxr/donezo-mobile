package com.jasawira.donezo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.CardWithChecklistItems
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.ChecklistRepository
import com.jasawira.donezo.notification.AlarmScheduler
import com.jasawira.donezo.presentation.uistate.CardDetailUiEvent
import com.jasawira.donezo.presentation.uistate.CardDetailUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * CardDetailViewModel
 * Mengelola state untuk Card Detail Screen
 * 
 * Fitur:
 * - Fetch card dengan items-nya
 * - CRUD operations untuk items
 * - Check/uncheck items
 * - Drag-drop reorder items
 * - Edit card (name, color, category)
 * - Schedule/cancel notifications
 */
@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val checklistRepository: ChecklistRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    // UI STATE
    private val _uiState = MutableStateFlow<CardDetailUiState>(CardDetailUiState.Loading)
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    // SNACKBAR EVENT
    private val _snackbarEvent = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    // CURRENT CARD ID
    private val _currentCardId = MutableStateFlow<String?>(null)
    val currentCardId: StateFlow<String?> = _currentCardId.asStateFlow()

    /**
     * Load card detail dengan items
     */
    fun loadCardDetail(cardId: String) {
        _currentCardId.value = cardId
        
        viewModelScope.launch {
            try {
                _uiState.value = CardDetailUiState.Loading

                // Combine card data dengan items
                checklistRepository.getItemsByCard(cardId).collect { items ->
                    val cardWithItems = cardRepository.getCardWithItems(cardId)
                    
                    _uiState.value = CardDetailUiState.Success(
                        cardWithItems = cardWithItems,
                        isEditingCard = false,
                        editingItemId = null,
                        isDarkMode = false // TODO: Get from settings
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CardDetailUiState.Error(
                    e.message ?: "Gagal memuat card detail"
                )
            }
        }
    }

    /**
     * Handle UI Events
     */
    fun onEvent(event: CardDetailUiEvent) {
        when (event) {
            is CardDetailUiEvent.UpdateCardName -> updateCardName(event.cardId, event.newName)
            is CardDetailUiEvent.UpdateCardColor -> updateCardColor(event.cardId, event.colorPresetId)
            is CardDetailUiEvent.UpdateCardCategory -> updateCardCategory(event.cardId, event.newCategoryId)
            is CardDetailUiEvent.ChecklistItemStatusChanged -> toggleItemStatus(event.itemId, event.isChecked)
            is CardDetailUiEvent.DeleteItem -> deleteItem(event.itemId)
            is CardDetailUiEvent.ReorderItem -> reorderItem(event.itemId, event.newPosition)
            is CardDetailUiEvent.UpdateItemPosition -> updateItemPositions(event.fromPosition, event.toPosition)
            is CardDetailUiEvent.AddItem -> addItem(event.item)
            is CardDetailUiEvent.UpdateItem -> updateItem(event.item)
            is CardDetailUiEvent.DeleteCard -> deleteCard()
            is CardDetailUiEvent.RefreshCardDetail -> refreshCardDetail()
        }
    }

    /**
     * Update card name
     */
    private fun updateCardName(cardId: String, newName: String) {
        if (newName.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama card tidak boleh kosong"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(
                        name = newName
                    )
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Nama card berhasil diupdate"))
                    loadCardDetail(cardId)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update nama card"))
            }
        }
    }

    /**
     * Update card color
     */
    private fun updateCardColor(cardId: String, colorPresetId: Int) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(
                        colorPresetId = colorPresetId
                    )
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Warna card berhasil diubah"))
                    loadCardDetail(cardId)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update warna card"))
            }
        }
    }

    /**
     * Update card category
     */
    private fun updateCardCategory(cardId: String, newCategoryId: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(
                        categoryId = newCategoryId
                    )
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Kategori card berhasil diubah"))
                    loadCardDetail(cardId)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update kategori"))
            }
        }
    }

    /**
     * Toggle item checked status
     * Trigger confetti animation saat check
     */
    private fun toggleItemStatus(itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItemCheckedStatus(itemId, isChecked)
                
                if (isChecked) {
                    _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil diselesaikan! 🎉"))
                } else {
                    _snackbarEvent.emit(SnackbarEvent.Info("Item ditandai belum selesai"))
                }

                // Refresh untuk update progress
                _currentCardId.value?.let { loadCardDetail(it) }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update item status"))
            }
        }
    }

    /**
     * Delete item dengan konfirmasi
     */
    private fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                // Get item detail untuk notif cancel
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val item = currentState.cardWithItems.items.find { it.id == itemId }
                    
                    // Cancel notifikasi jika ada
                    if (item?.isNotificationEnabled == true) {
                        alarmScheduler.cancelItemNotifications(itemId)
                    }

                    // Delete item
                    checklistRepository.deleteItem(itemId)
                    _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil dihapus"))

                    // Refresh
                    _currentCardId.value?.let { loadCardDetail(it) }
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal delete item"))
            }
        }
    }

    /**
     * Reorder item ke posisi baru
     */
    private fun reorderItem(itemId: String, newPosition: Int) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItemPosition(itemId, newPosition)
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal mengubah urutan item"))
            }
        }
    }

    /**
     * Update posisi items saat drag-drop
     */
    private fun updateItemPositions(fromPosition: Int, toPosition: Int) {
        val currentState = _uiState.value
        if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
            val items = currentState.cardWithItems.items.toMutableList()
            if (fromPosition < items.size && toPosition < items.size) {
                val movedItem = items.removeAt(fromPosition)
                items.add(toPosition, movedItem)

                // Update database dengan posisi baru
                viewModelScope.launch {
                    try {
                        items.forEachIndexed { index, item ->
                            checklistRepository.updateItemPosition(item.id, index)
                        }
                    } catch (e: Exception) {
                        _snackbarEvent.emit(SnackbarEvent.Error("Gagal mengubah urutan"))
                    }
                }
            }
        }
    }

    /**
     * Add new item
     */
    private fun addItem(item: ChecklistItem) {
        if (item.itemName.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama item tidak boleh kosong"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val newItem = item.copy(
                    id = UUID.randomUUID().toString(),
                    createdAt = LocalDateTime.now()
                )

                checklistRepository.addItem(newItem)

                // Schedule notifikasi jika enabled
                if (newItem.isNotificationEnabled && newItem.deadline != null && newItem.notificationTime != null) {
                    val deadlineDateTime = LocalDateTime.of(
                        newItem.deadline,
                        newItem.notificationTime
                    )
                    
                    val currentState = _uiState.value
                    if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                        alarmScheduler.scheduleItemNotifications(
                            itemId = newItem.id,
                            cardName = currentState.cardWithItems.card.name,
                            itemName = newItem.itemName,
                            deadline = deadlineDateTime,
                            minutesBefore = newItem.notificationMinutesBefore
                        )
                    }
                }

                _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil ditambahkan"))

                // Refresh
                _currentCardId.value?.let { loadCardDetail(it) }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal menambah item"))
            }
        }
    }

    /**
     * Update existing item
     */
    private fun updateItem(item: ChecklistItem) {
        if (item.itemName.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama item tidak boleh kosong"))
            }
            return
        }

        viewModelScope.launch {
            try {
                checklistRepository.updateItem(item)

                // Reschedule notifikasi jika ada perubahan deadline/time
                if (item.isNotificationEnabled && item.deadline != null && item.notificationTime != null) {
                    val deadlineDateTime = LocalDateTime.of(
                        item.deadline,
                        item.notificationTime
                    )

                    val currentState = _uiState.value
                    if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                        alarmScheduler.rescheduleItemNotifications(
                            itemId = item.id,
                            cardName = currentState.cardWithItems.card.name,
                            itemName = item.itemName,
                            newDeadline = deadlineDateTime,
                            minutesBefore = item.notificationMinutesBefore
                        )
                    }
                } else {
                    // Cancel notifikasi jika disable
                    alarmScheduler.cancelItemNotifications(item.id)
                }

                _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil diupdate"))

                // Refresh
                _currentCardId.value?.let { loadCardDetail(it) }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update item"))
            }
        }
    }

    /**
     * Delete card
     */
    private fun deleteCard() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val cardId = currentState.cardWithItems.card.id
                    
                    // Cancel semua notifikasi untuk items dalam card ini
                    currentState.cardWithItems.items.forEach { item ->
                        if (item.isNotificationEnabled) {
                            alarmScheduler.cancelItemNotifications(item.id)
                        }
                    }

                    // Delete card (items akan cascade delete)
                    cardRepository.deleteCard(cardId)
                    _snackbarEvent.emit(SnackbarEvent.Success("Card berhasil dihapus"))
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal delete card"))
            }
        }
    }

    /**
     * Refresh card detail
     */
    private fun refreshCardDetail() {
        _currentCardId.value?.let { loadCardDetail(it) }
    }
}
