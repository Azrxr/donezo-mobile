package com.jasawira.donezo.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.presentation.components.*
import com.jasawira.donezo.presentation.components.ChecklistItemPreview
import com.jasawira.donezo.presentation.uistate.HomeUiEvent
import com.jasawira.donezo.presentation.uistate.HomeUiState
import com.jasawira.donezo.presentation.uistate.CardPreview
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * HomeScreen - Redesigned
 * Main screen dengan design baru sesuai gambar
 * Features:
 * - Long press untuk masuk edit mode
 * - Multi-select cards
 * - Drag-drop reorder
 * - Delete dengan konfirmasi dialog
 */
@Composable
fun HomeScreenRedesign(
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

    // Edit mode state dari UI
    val isEditMode = (uiState as? HomeUiState.Success)?.isEditMode ?: false
    val selectedCardIds = (uiState as? HomeUiState.Success)?.selectedCardIds ?: emptySet()

    // BackHandler untuk keluar edit mode dengan gesture back
    BackHandler(enabled = isEditMode) {
        viewModel.onEvent(HomeUiEvent.ExitEditMode)
    }

    // Listen to snackbar events
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
            // Edit Mode Top Bar
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
            // Hide FAB when in edit mode
            if (!isEditMode) {
                FloatingActionButton(
                    onClick = { showAddCardSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Card")
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        // Box untuk detect tap di area kosong
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
                    } else {
                        Modifier
                    }
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
            // Top App Bar dengan logo, search, dan settings
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

            // Welcome Header
            item {
                WelcomeHeader(
                    userName = userName,
                    onUserNameChange = { newName ->
                        viewModel.updateUserName(newName)
                    }
                )
            }


            // Category Filter Chips
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

            // Content based on UI State
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is HomeUiState.Success -> {
                    val cards = state.filteredCards
                    val cardPreviews = state.cardPreviews

                    if (cards.isEmpty()) {
                        // Show create card placeholder when empty
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                            CreateCardPlaceholder(
                                onClick = { showAddCardSheet = true }
                            )
                        }
                    } else {
                        // Show cards with Edit Mode support
                        itemsIndexed(
                            items = cards,
                            key = { _, card -> card.id }
                        ) { index, card ->
                            val isSelected = selectedCardIds.contains(card.id)
                            // Find preview items for this card
                            val cardPreview = cardPreviews.find { it.card.id == card.id }
                            val previewItems = cardPreview?.previewItems?.map { item ->
                                ChecklistItemPreview(
                                    id = item.id,
                                    name = item.itemName,
                                    isChecked = item.isChecked
                                )
                            } ?: emptyList()

                            EditableCardWrapper(
                                isEditMode = isEditMode,
                                isSelected = isSelected,
                                onLongPress = {
                                    // Enter edit mode on long press
                                    if (!isEditMode) {
                                        viewModel.onEvent(HomeUiEvent.EnterEditMode)
                                    }
                                    // Auto select the long-pressed card
                                    viewModel.onEvent(HomeUiEvent.ToggleCardSelection(card.id))
                                },
                                onTap = {
                                    if (isEditMode) {
                                        // Toggle selection in edit mode
                                        viewModel.onEvent(HomeUiEvent.ToggleCardSelection(card.id))
                                    } else {
                                        // Navigate to detail
                                        onCardClick(card.id)
                                    }
                                }
                            ) {
                                CompactCardComponent(
                                    cardName = card.name,
                                    colorPresetId = card.colorPresetId,
                                    completedCount = card.completedItemCount,
                                    totalCount = card.itemCount,
                                    items = previewItems,
                                    onCardClick = {
                                        if (isEditMode) {
                                            viewModel.onEvent(HomeUiEvent.ToggleCardSelection(card.id))
                                        } else {
                                            onCardClick(card.id)
                                        }
                                    },
                                    onItemCheckChange = { itemId, isChecked ->
                                        // TODO: Handle check change
                                    }
                                )
                            }
                        }

                        // Add placeholder at bottom (hide when in edit mode)
                        if (!isEditMode) {
                            item {
                                CreateCardPlaceholder(
                                    onClick = { showAddCardSheet = true }
                                )
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }

                is HomeUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Error: ${state.message}")
                                Button(onClick = { viewModel.onEvent(HomeUiEvent.RefreshCards) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
        } // Close Box wrapper
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Card") },
            text = {
                Text("Apakah Anda yakin ingin menghapus ${selectedCardIds.size} card yang dipilih?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(HomeUiEvent.DeleteSelectedCards)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Add Card Dialog
    if (showAddCardSheet) {
        AddCardDialogRedesigned(
            categories = (uiState as? HomeUiState.Success)?.categories ?: emptyList(),
            onDismiss = { showAddCardSheet = false },
            onAddCard = { name, categoryId, colorPresetId ->
                viewModel.addCard(name, categoryId, colorPresetId)
                showAddCardSheet = false
            },
            onAddCategory = { categoryName ->
                viewModel.addCategory(categoryName)
            }
        )
    }
}

/**
 * EditModeTopBar
 * Top bar saat dalam edit mode dengan opsi select all, delete, close
 */
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
                text = if (selectedCount > 0) "$selectedCount dipilih" else "Mode Edit",
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Tutup")
            }
        },
        actions = {
            // Select/Deselect All
            TextButton(onClick = {
                if (selectedCount > 0) onDeselectAll() else onSelectAll()
            }) {
                Text(if (selectedCount > 0) "Batal Pilih" else "Pilih Semua")
            }

            // Delete Button (only show when items selected)
            if (selectedCount > 0) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color.Red
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

/**
 * EditableCardWrapper
 * Wrapper untuk card dengan edit mode support:
 * - Rotation shake animation seperti iOS (natural)
 * - Visual feedback saat ditekan (elevation + scale)
 * - Selection indicator dengan border
 * - Long press untuk masuk edit mode
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

    // Rotation animation for edit mode (like iOS)
    val rotationAngle = remember { Animatable(0f) }

    // Random initial offset agar setiap card goyang beda fase
    val randomDelay = remember { (0..150).random() }
    // Random amplitude agar setiap card sedikit beda intensitas
    val amplitude = remember { 1.5f + (0..10).random() / 10f }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            kotlinx.coroutines.delay(randomDelay.toLong())
            rotationAngle.animateTo(
                targetValue = amplitude,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 120 + (0..60).random(),
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            rotationAngle.snapTo(0f)
        }
    }

    var isPressed by remember { mutableStateOf(false) }

    // Reset state saat edit mode berubah
    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            isPressed = false
        }
    }

    // Animated values
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF26D3C8) else Color.Transparent,
        label = "border"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 1.02f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    val cardElevation by animateFloatAsState(
        targetValue = if (isPressed) 4f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "elevation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = if (isEditMode) rotationAngle.value else 0f
                scaleX = cardScale
                scaleY = cardScale
                shadowElevation = cardElevation
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onTap()
                    },
                    onLongPress = {
                        // Haptic feedback saat long press
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                )
            }
    ) {
        // Main content with selection border only
        Box(
            modifier = Modifier
                .then(
                    if (isSelected) {
                        Modifier
                            .padding(4.dp)
                            .border(3.dp, borderColor, RoundedCornerShape(20.dp))
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
    }
}

/**
 * AddCardBottomSheetSimple
 * Simplified version without color picker (auto random)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheetSimple(
    categories: List<com.jasawira.donezo.domain.model.Category>,
    onDismiss: () -> Unit = {},
    onAddCard: (name: String, categoryId: String) -> Unit = { _, _ -> }
) {
    var cardName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Buat Card Baru", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = cardName,
                onValueChange = { cardName = it },
                label = { Text("Nama Card") },
                modifier = Modifier.fillMaxWidth()
            )

            if (categories.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.id == selectedCategoryId }?.name ?: "Pilih Kategori",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (cardName.isNotBlank() && selectedCategoryId.isNotBlank()) {
                            onAddCard(cardName, selectedCategoryId)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Buat")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Batal")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



