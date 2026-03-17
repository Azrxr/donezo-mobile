package com.jasawira.donezo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.presentation.uistate.CategoryUiEvent
import com.jasawira.donezo.presentation.uistate.CategoryUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * CategoryViewModel
 * Mengelola state untuk Category Management Screen
 * * Fitur:
 * - Fetch all categories
 * - Add category baru
 * - Update/Edit category
 * - Delete category (Auto-migrate cards to Uncategorized)
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val cardRepository: CardRepository // Ditambahkan untuk memigrasi Card saat kategori dihapus
) : ViewModel() {

    // UI STATE
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    // SNACKBAR EVENT
    private val _snackbarEvent = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    init {
        loadCategories()
    }

    /**
     * Load semua categories
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                _uiState.value = CategoryUiState.Loading

                categoryRepository.getAllCategories().collect { categories ->
                    _uiState.value = CategoryUiState.Success(
                        categories = categories,
                        isAddingCategory = false,
                        editingCategoryId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error(
                    e.message ?: "Gagal memuat kategori"
                )
            }
        }
    }

    /**
     * Handle UI Events
     */
    fun onEvent(event: CategoryUiEvent) {
        when (event) {
            is CategoryUiEvent.AddCategory -> addCategory(event.categoryName)
            is CategoryUiEvent.UpdateCategory -> updateCategory(event.categoryId, event.newName)
            is CategoryUiEvent.DeleteCategory -> deleteCategory(event.categoryId)
            is CategoryUiEvent.RefreshCategories -> loadCategories()
        }
    }

    /**
     * Add kategori baru
     */
    fun addCategory(categoryName: String) {
        if (categoryName.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama kategori tidak boleh kosong"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val newCategory = Category(
                    id = UUID.randomUUID().toString(),
                    name = categoryName.trim(),
                    createdAt = LocalDateTime.now()
                )

                categoryRepository.addCategory(newCategory)
                _snackbarEvent.emit(SnackbarEvent.Success("Kategori berhasil ditambahkan"))

                // Refresh list
                loadCategories()
            } catch (e: Exception) {
                _snackbarEvent.emit(
                    SnackbarEvent.Error("Gagal menambah kategori: ${e.message}")
                )
            }
        }
    }

    /**
     * Update kategori
     */
    fun updateCategory(categoryId: String, newName: String) {
        if (newName.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarEvent.Error("Nama kategori tidak boleh kosong"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CategoryUiState.Success) {
                    val category = currentState.categories.find { it.id == categoryId }
                    if (category != null) {
                        val updatedCategory = category.copy(
                            name = newName.trim()
                        )
                        categoryRepository.updateCategory(updatedCategory)
                        _snackbarEvent.emit(SnackbarEvent.Success("Kategori berhasil diupdate"))
                        loadCategories()
                    }
                }
            } catch (e: Exception) {
                _snackbarEvent.emit(
                    SnackbarEvent.Error("Gagal update kategori")
                )
            }
        }
    }

    /**
     * Delete kategori dengan Logika Migrasi Card Cerdas
     */
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val categoryToDelete = if (currentState is CategoryUiState.Success) {
                    currentState.categories.find { it.id == categoryId }
                } else null

                // Mencegah penghapusan kategori default "Uncategorized"
                if (categoryToDelete?.name?.equals("Uncategorized", ignoreCase = true) == true) {
                    _snackbarEvent.emit(SnackbarEvent.Error("Kategori bawaan 'Uncategorized' tidak dapat dihapus"))
                    return@launch
                }

                // 1. Pastikan kategori "Uncategorized" ada di database. Jika tidak, buatkan.
                val allCategories = categoryRepository.getAllCategories().first()
                var uncategorized = allCategories.find { it.name.equals("Uncategorized", ignoreCase = true) }

                if (uncategorized == null) {
                    uncategorized = Category(
                        id = UUID.randomUUID().toString(),
                        name = "Uncategorized",
                        createdAt = LocalDateTime.now()
                    )
                    categoryRepository.addCategory(uncategorized)
                }

                // 2. Ambil semua Card yang masih menggunakan kategori yang akan dihapus ini
                val cardsToMigrate = cardRepository.getCardsByCategory(categoryId).first()

                // 3. Pindahkan (Update) categoryId semua Card tersebut ke Uncategorized
                for (card in cardsToMigrate) {
                    val updatedCard = card.copy(categoryId = uncategorized.id)
                    cardRepository.updateCard(updatedCard)
                }

                // 4. Setelah Card aman, hapus kategori secara permanen
                categoryRepository.deleteCategory(categoryId)

                if (cardsToMigrate.isNotEmpty()) {
                    _snackbarEvent.emit(SnackbarEvent.Success("Kategori dihapus & ${cardsToMigrate.size} tugas dipindah ke Uncategorized"))
                } else {
                    _snackbarEvent.emit(SnackbarEvent.Success("Kategori berhasil dihapus"))
                }

                loadCategories()
            } catch (e: Exception) {
                _snackbarEvent.emit(SnackbarEvent.Error("Gagal menghapus kategori"))
            }
        }
    }

    /**
     * Get current categories untuk UI
     */
    fun getCurrentCategories(): List<Category> {
        return (uiState.value as? CategoryUiState.Success)?.categories ?: emptyList()
    }
}