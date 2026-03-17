package com.jasawira.donezo.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.presentation.components.CardDialog
import com.jasawira.donezo.presentation.components.CategoryFilterChips
import com.jasawira.donezo.presentation.components.ChecklistItemPreview
import com.jasawira.donezo.presentation.components.CompactCardComponent
import com.jasawira.donezo.presentation.components.CreateCardPlaceholder
import com.jasawira.donezo.presentation.components.TopAppBarDonezo
import com.jasawira.donezo.presentation.components.WelcomeHeader
import com.jasawira.donezo.presentation.uistate.HomeUiEvent
import com.jasawira.donezo.presentation.uistate.HomeUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddCardSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val isEditMode = (uiState as? HomeUiState.Success)?.isEditMode ?: false
    val selectedCardIds = (uiState as? HomeUiState.Success)?.selectedCardIds ?: emptySet()

    BackHandler(enabled = isEditMode) {
        viewModel.onEvent(HomeUiEvent.ExitEditMode)
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { event ->
            scope.launch {
                when (event) {
                    is SnackbarEvent.Success -> snackbarHostState.showSnackbar(event.message)
                    is SnackbarEvent.Error -> snackbarHostState.showSnackbar(event.message)
                    is SnackbarEvent.Info -> snackbarHostState.showSnackbar(event.message)
                    else -> {}
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isEditMode) {
                EditModeTopBar(
                    selectedCount = selectedCardIds.size,
                    onSelectAll = { viewModel.onEvent(HomeUiEvent.SelectAllCards) },
                    onDeselectAll = { viewModel.onEvent(HomeUiEvent.DeselectAllCards) },
                    onDelete = { showDeleteConfirmDialog = true },
                    onClose = { viewModel.onEvent(HomeUiEvent.ExitEditMode) }
                )
            }
        },
        floatingActionButton = {
            if (!isEditMode) {
                FloatingActionButton(
                    onClick = { showAddCardSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Card")
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .then(
                    if (isEditMode) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.onEvent(HomeUiEvent.ExitEditMode)
                        }
                    } else Modifier
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    TopAppBarDonezo(
                        searchValue = searchQuery,
                        onSearchChange = { query ->
                            searchQuery = query
                            viewModel.onEvent(HomeUiEvent.SearchCards(query))
                        },
                        onSettingsClick = onSettingsClick
                    )
                }

                item {
                    WelcomeHeader(
                        userName = userName,
                        onUserNameChange = { newName ->
                            viewModel.updateUserName(newName)
                        }
                    )
                }

                item {
                    when (val state = uiState) {
                        is HomeUiState.Success -> {
                            CategoryFilterChips(
                                categories = state.categories,
                                selectedCategoryId = state.filterOptions.categoryId,
                                onCategorySelected = { categoryId ->
                                    if (categoryId == null) {
                                        viewModel.onEvent(HomeUiEvent.ClearFilter)
                                    } else {
                                        viewModel.onEvent(HomeUiEvent.FilterByCategory(categoryId))
                                    }
                                }
                            )
                        }
                        else -> {}
                    }
                }

                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is HomeUiState.Success -> {
                        val cards = state.filteredCards
                        val cardPreviews = state.cardPreviews

                        if (cards.isEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                                CreateCardPlaceholder(onClick = { showAddCardSheet = true })
                            }
                        } else {
                            items(items = cards, key = { card -> card.id }) { card ->
                                val isSelected = selectedCardIds.contains(card.id)
                                val cardPreview = cardPreviews.find { it.card.id == card.id }
                                val previewItems = cardPreview?.previewItems?.map { item ->
                                    ChecklistItemPreview(id = item.id, name = item.itemName, isChecked = item.isChecked)
                                } ?: emptyList()

                                EditableCardWrapper(
                                    isEditMode = isEditMode,
                                    isSelected = isSelected,
                                    onLongPress = {
                                        if (!isEditMode) {
                                            viewModel.onEvent(HomeUiEvent.EnterEditMode)
                                            viewModel.onEvent(HomeUiEvent.ToggleCardSelection(card.id))
                                        }
                                    },
                                    onTap = {
                                        if (isEditMode) {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.onEvent(HomeUiEvent.ToggleCardSelection(card.id))
                                        } else {
                                            onCardClick(card.id)
                                        }
                                    }
                                ) {
                                    CompactCardComponent(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        cardName = card.name,
                                        colorPresetId = card.colorPresetId,
                                        completedCount = card.completedItemCount,
                                        totalCount = card.itemCount,
                                        categoryName = state.categories.find { it.id == card.categoryId }?.name ?: "Tanpa Kategori",
                                        items = previewItems,
                                        
                                    )
                                }
                            }

                            if (!isEditMode) {
                                item {
                                    CreateCardPlaceholder(onClick = { showAddCardSheet = true })
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Error: ${state.message}")
                                    Button(onClick = { viewModel.onEvent(HomeUiEvent.RefreshCards) }) { Text("Retry") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Tugas") },
            text = { Text("Apakah Anda yakin ingin menghapus ${selectedCardIds.size} tugas yang dipilih?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(HomeUiEvent.DeleteSelectedCards)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showAddCardSheet) {
        CardDialog(
            categories = (uiState as? HomeUiState.Success)?.categories ?: emptyList(),
            onDismiss = { showAddCardSheet = false },
            onSave = { name, existingCategoryId, newCategoryName, colorPresetId ->
                // Logika Cerdas: Simpan Kategori Baru jika ada teks, jika tidak gunakan Kategori lama
                val finalCategoryId = if (!newCategoryName.isNullOrBlank()) {
                    viewModel.addCategory(newCategoryName) // Ini akan mengembalikan UUID Kategori baru
                } else {
                    existingCategoryId ?: ""
                }

                // Simpan Card menggunakan finalCategoryId (Foreign Key)
                viewModel.addCard(name, finalCategoryId, colorPresetId)
                showAddCardSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditModeTopBar(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (selectedCount > 0) "$selectedCount dipilih" else "Pilih Tugas",
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Tutup")
            }
        },
        actions = {
            TextButton(onClick = { if (selectedCount > 0) onDeselectAll() else onSelectAll() }) {
                Text(if (selectedCount > 0) "Batal" else "Pilih Semua", color = MaterialTheme.colorScheme.primary)
            }
            if (selectedCount > 0) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/**
 * EditableCardWrapper
 * Didesain ulang agar TANPA border tebal & overlay putih.
 * Hanya mengandalkan efek jiggle, scale-down, dan icon kecil.
 */
@Composable
fun EditableCardWrapper(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
    content: @Composable () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    val rotationAngle = remember { Animatable(0f) }
    val randomDelay = remember { (0..150).random() }
    val amplitude = remember { 1.2f + (0..5).random() / 10f }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            delay(randomDelay.toLong())
            rotationAngle.animateTo(
                targetValue = amplitude,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 140, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            rotationAngle.snapTo(0f)
        }
    }

    var isPressed by remember { mutableStateOf(false) }
    LaunchedEffect(isEditMode) { if (!isEditMode) isPressed = false }

    // Efek scale yang premium (mengempis sedikit saat ditekan atau saat edit mode)
    val cardScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isEditMode -> 0.98f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp) // Sedikit padding untuk ruang gerak animasi
            .graphicsLayer {
                rotationZ = if (isEditMode) rotationAngle.value else 0f
                scaleX = cardScale
                scaleY = cardScale
            }
            .pointerInput(isEditMode) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onTap() },
                    onLongPress = {
                        if (!isEditMode) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPress()
                        }
                    }
                )
            }
    ) {
        // Konten utama Card
        content()

        // Indikator centang HANYA SAAT EDIT MODE (Tanpa Background Overlay Penuh)
        if (isEditMode) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = if (isSelected) "Selected" else "Unselected",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp) // Jarak dari ujung Card
                    .size(28.dp)
                    .background(Color.White, CircleShape) // Background putih hanya seukuran ikon agar menonjol
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun homeScreenPrev(){
    HomeScreen {  }
}