package com.jasawira.donezo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.presentation.uistate.CategoryUiState
import com.jasawira.donezo.utils.UuidGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * CategoryViewModel
 * Mengelola state untuk Category Management
 * Fitur: Load categories, CRUD operations
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getAllCategories()
                    .map { categories ->
                        CategoryUiState.Success(
                            categories = categories,
                            isAddingCategory = false,
                            editingCategoryId = null
                        )
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error("Gagal load categories: ${e.message}")
            }
        }
    }

    fun addCategory(categoryName: String) {
        if (categoryName.isBlank()) {
            _uiState.value = CategoryUiState.Error("Nama kategori tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            try {
                val category = Category(
                    id = UuidGenerator.generateId("cat"),
                    name = categoryName,
                    createdAt = LocalDateTime.now()
                )
                categoryRepository.addCategory(category)
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error("Gagal menambah kategori: ${e.message}")
            }
        }
    }

    fun updateCategory(categoryId: String, newName: String) {
        if (newName.isBlank()) {
            _uiState.value = CategoryUiState.Error("Nama kategori tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is CategoryUiState.Success) {
                    val categoryToUpdate = currentState.categories.find { it.id == categoryId }
                    if (categoryToUpdate != null) {
                        val updated = categoryToUpdate.copy(name = newName)
                        categoryRepository.updateCategory(updated)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error("Gagal update kategori: ${e.message}")
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryId)
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error("Gagal hapus kategori: ${e.message}")
            }
        }
    }

    fun refreshCategories() {
        loadCategories()
    }
}

