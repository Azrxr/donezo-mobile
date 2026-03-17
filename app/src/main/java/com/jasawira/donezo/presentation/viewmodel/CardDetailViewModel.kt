package com.jasawira.donezo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.domain.repository.ChecklistRepository
import com.jasawira.donezo.notification.AlarmScheduler
import com.jasawira.donezo.presentation.uistate.CardDetailUiEvent
import com.jasawira.donezo.presentation.uistate.CardDetailUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val checklistRepository: ChecklistRepository,
    private val categoryRepository: CategoryRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<CardDetailUiState>(CardDetailUiState.Loading)
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private val _currentCardId = MutableStateFlow<String?>(null)
    val currentCardId: StateFlow<String?> = _currentCardId.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    private val _newItemName = MutableStateFlow("")
    val newItemName: StateFlow<String> = _newItemName.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private var detailJob: Job? = null

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect {
                _categories.value = it
            }
        }
    }

    fun loadCardDetail(cardId: String) {
        _currentCardId.value = cardId

        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            try {
                if (_uiState.value !is CardDetailUiState.Success) {
                    _uiState.value = CardDetailUiState.Loading
                }

                // Flow ini otomatis mengirim pembaruan setiap kali Database Items berubah!
                checklistRepository.getItemsByCard(cardId).collect { items ->
                    val cardWithItems = cardRepository.getCardWithItems(cardId)

                    _uiState.value = CardDetailUiState.Success(
                        cardWithItems = cardWithItems,
                        isEditingCard = false,
                        editingItemId = null,
                        isDarkMode = false
                    )
                }
            } catch (e: Exception) {
                // PENTING: Abaikan jika error disebabkan oleh job yang sengaja di-cancel
                if (e is CancellationException) throw e

                _uiState.value = CardDetailUiState.Error(
                    e.message ?: "Gagal memuat card detail"
                )
            }
        }
    }

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

    private fun updateCardName(cardId: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(name = newName)
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Nama card berhasil diupdate"))
                    loadCardDetail(cardId) // Perlu direfresh karena Flow di atas mendeteksi Item, bukan Card
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update nama card"))
            }
        }
    }

    private fun updateCardColor(cardId: String, colorPresetId: Int) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(colorPresetId = colorPresetId)
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Warna card berhasil diubah"))
                    loadCardDetail(cardId)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update warna card"))
            }
        }
    }

    private fun updateCardCategory(cardId: String, newCategoryId: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(categoryId = newCategoryId)
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Kategori card berhasil diubah"))
                    loadCardDetail(cardId)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update kategori"))
            }
        }
    }

    fun updateCardDetails(cardId: String, name: String, categoryId: String, colorPresetId: Int) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val updatedCard = currentState.cardWithItems.card.copy(
                        name = name,
                        categoryId = categoryId,
                        colorPresetId = colorPresetId
                    )
                    cardRepository.updateCard(updatedCard)
                    _snackbarEvent.emit(SnackbarEvent.Success("Detail tugas berhasil disimpan"))
                    loadCardDetail(cardId)
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal menyimpan perubahan"))
            }
        }
    }

    fun addCategory(name: String): String {
        val categoryId = UUID.randomUUID().toString()
        viewModelScope.launch {
            try {
                val category = Category(id = categoryId, name = name, createdAt = LocalDateTime.now())
                categoryRepository.addCategory(category)
                _snackbarEvent.emit(SnackbarEvent.Success("Kategori '$name' berhasil ditambahkan"))
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal menambah kategori"))
            }
        }
        return categoryId
    }

    // --- OPERASI ITEM CHECKLIST (Tanpa pemanggilan loadCardDetail() manual karena Flow auto-update) ---

    private fun toggleItemStatus(itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItemCheckedStatus(itemId, isChecked)
                if (isChecked) {
                    _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil diselesaikan! 🎉"))
                }
                // Dihapus: _currentCardId.value?.let { loadCardDetail(it) } -> Flow akan merespon secara otomatis!
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update item status"))
            }
        }
    }

    private fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val item = currentState.cardWithItems.items.find { it.id == itemId }
                    if (item?.isNotificationEnabled == true) alarmScheduler.cancelItemNotifications(itemId)
                    checklistRepository.deleteItem(itemId)
                    _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil dihapus"))
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal delete item"))
            }
        }
    }

    private fun reorderItem(itemId: String, newPosition: Int) {
        viewModelScope.launch {
            try {
                checklistRepository.updateItemPosition(itemId, newPosition)
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal mengubah urutan item"))
            }
        }
    }

    private fun updateItemPositions(fromPosition: Int, toPosition: Int) {
        val currentState = _uiState.value
        if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
            val items = currentState.cardWithItems.items.toMutableList()
            if (fromPosition < items.size && toPosition < items.size) {
                val movedItem = items.removeAt(fromPosition)
                items.add(toPosition, movedItem)
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

    private fun addItem(item: ChecklistItem) {
        if (item.itemName.isBlank()) return
        viewModelScope.launch {
            try {
                val newItem = item.copy(id = UUID.randomUUID().toString(), createdAt = LocalDateTime.now())
                checklistRepository.addItem(newItem)
                if (newItem.isNotificationEnabled && newItem.deadline != null && newItem.notificationTime != null) {
                    val deadlineDateTime = LocalDateTime.of(newItem.deadline, newItem.notificationTime)
                    val currentState = _uiState.value
                    if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                        alarmScheduler.scheduleItemNotifications(
                            newItem.id, currentState.cardWithItems.card.name, newItem.itemName, deadlineDateTime, newItem.notificationMinutesBefore
                        )
                    }
                }
                _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil ditambahkan"))
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal menambah item"))
            }
        }
    }

    private fun updateItem(item: ChecklistItem) {
        if (item.itemName.isBlank()) return
        viewModelScope.launch {
            try {
                checklistRepository.updateItem(item)
                if (item.isNotificationEnabled && item.deadline != null && item.notificationTime != null) {
                    val deadlineDateTime = LocalDateTime.of(item.deadline, item.notificationTime)
                    val currentState = _uiState.value
                    if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                        alarmScheduler.rescheduleItemNotifications(
                            item.id, currentState.cardWithItems.card.name, item.itemName, deadlineDateTime, item.notificationMinutesBefore
                        )
                    }
                } else {
                    alarmScheduler.cancelItemNotifications(item.id)
                }
                _snackbarEvent.emit(SnackbarEvent.Success("Item berhasil diupdate"))
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal update item"))
            }
        }
    }

    private fun deleteCard() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val cardId = currentState.cardWithItems.card.id
                    currentState.cardWithItems.items.forEach { item ->
                        if (item.isNotificationEnabled) alarmScheduler.cancelItemNotifications(item.id)
                    }
                    cardRepository.deleteCard(cardId)
                    _snackbarEvent.emit(SnackbarEvent.Success("Card berhasil dihapus"))
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal delete card"))
            }
        }
    }

    fun toggleItemSelection(itemId: String) {
        _selectedItems.value = if (_selectedItems.value.contains(itemId)) {
            _selectedItems.value - itemId
        } else {
            _selectedItems.value + itemId
        }
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            var successCount = 0
            _selectedItems.value.forEach { itemId ->
                try {
                    if (checklistRepository.deleteItem(itemId)) successCount++
                } catch (e: Exception) {}
            }
            clearSelection()
            if (successCount > 0) _snackbarEvent.emit(SnackbarEvent.Success("$successCount item berhasil dihapus"))
        }
    }

    fun markSelectedItemsAsCompleted() {
        viewModelScope.launch {
            _selectedItems.value.forEach { itemId ->
                try {
                    val item = (_uiState.value as? CardDetailUiState.Success)?.cardWithItems?.items?.find { it.id == itemId }
                    if (item != null) onEvent(CardDetailUiEvent.ChecklistItemStatusChanged(itemId, true))
                } catch (e: Exception) {}
            }
            clearSelection()
            _snackbarEvent.emit(SnackbarEvent.Success("Items ditandai selesai"))
        }
    }

    fun updateNewItemName(name: String) {
        _newItemName.value = name
    }

    fun addNewItemInline() {
        if (_newItemName.value.isBlank()) return
        _currentCardId.value?.let { cardId ->
            val newItem = ChecklistItem(
                id = UUID.randomUUID().toString(),
                cardId = cardId,
                itemName = _newItemName.value,
                createdAt = LocalDateTime.now()
            )
            onEvent(CardDetailUiEvent.AddItem(newItem))
            _newItemName.value = ""
        }
    }

    fun reorderItems(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
                    val items = currentState.cardWithItems.items.toMutableList()
                    val item = items.removeAt(fromIndex)
                    items.add(toIndex, item)
                    items.forEachIndexed { index, checklistItem ->
                        checklistRepository.updateItemPosition(checklistItem.id, index)
                    }
                }
            } catch (e: Exception) {}
        }
    }

    private fun refreshCardDetail() {
        _currentCardId.value?.let { loadCardDetail(it) }
    }

    fun addNewItemWithDetails(
        itemName: String, deadline: java.time.LocalDate?, notificationTime: java.time.LocalTime?, isNotificationEnabled: Boolean, notificationMinutesBefore: Int
    ) {
        if (itemName.isBlank()) return
        _currentCardId.value?.let { cardId ->
            val newItem = ChecklistItem(
                id = UUID.randomUUID().toString(), cardId = cardId, itemName = itemName, deadline = deadline, notificationTime = notificationTime, notificationMinutesBefore = notificationMinutesBefore, isNotificationEnabled = isNotificationEnabled, createdAt = LocalDateTime.now()
            )
            onEvent(CardDetailUiEvent.AddItem(newItem))
        }
    }

    fun addNewItemSimple(
        itemName: String, deadlineDate: java.time.LocalDate?, deadlineTime: java.time.LocalTime?, reminderMinutesBefore: Int?
    ) {
        if (itemName.isBlank()) return
        _currentCardId.value?.let { cardId ->
            val isNotificationEnabled = reminderMinutesBefore != null && deadlineDate != null
            val newItem = ChecklistItem(
                id = UUID.randomUUID().toString(), cardId = cardId, itemName = itemName, deadline = deadlineDate, notificationTime = deadlineTime, notificationMinutesBefore = reminderMinutesBefore ?: 30, isNotificationEnabled = isNotificationEnabled, createdAt = LocalDateTime.now()
            )
            onEvent(CardDetailUiEvent.AddItem(newItem))
        }
    }

    fun updateItemSimple(
        itemId: String, itemName: String, deadlineDate: java.time.LocalDate?, deadlineTime: java.time.LocalTime?, reminderMinutesBefore: Int?
    ) {
        if (itemName.isBlank()) return
        val currentState = _uiState.value
        if (currentState is CardDetailUiState.Success && currentState.cardWithItems != null) {
            val existingItem = currentState.cardWithItems.items.find { it.id == itemId }
            if (existingItem != null) {
                val isNotificationEnabled = reminderMinutesBefore != null && deadlineDate != null
                val updatedItem = existingItem.copy(
                    itemName = itemName, deadline = deadlineDate, notificationTime = deadlineTime, notificationMinutesBefore = reminderMinutesBefore ?: 30, isNotificationEnabled = isNotificationEnabled
                )
                onEvent(CardDetailUiEvent.UpdateItem(updatedItem))
            }
        }
    }
}