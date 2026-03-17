@file:Suppress(
    "UNUSED_VARIABLE",
    "UNUSED_IMPORT",
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE",
    "UNUSED_VALUE"
)

package com.jasawira.donezo.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.presentation.components.*
import com.jasawira.donezo.presentation.theme.ColorPresets
import com.jasawira.donezo.presentation.theme.softBackground
import com.jasawira.donezo.presentation.theme.contentColor
import com.jasawira.donezo.presentation.theme.borderColor
import com.jasawira.donezo.presentation.theme.DonezoTheme
import com.jasawira.donezo.presentation.uistate.CardDetailUiEvent
import com.jasawira.donezo.presentation.uistate.CardDetailUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.CardDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * CardDetailScreenRedesign
 * Detail screen dengan design baru sesuai gambar dan Full Color Theming
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class) // <-- Tambahan Animate Item Placement
@Composable
fun CardDetailScreen(
    modifier: Modifier = Modifier,
    cardId: String,
    viewModel: CardDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    // STATE UNTUK ADD / EDIT BOTTOM SHEET ITEM
    var showSheet by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ChecklistItem?>(null) }

    // STATE UNTUK EDIT CARD UTAMA
    var showEditCardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cardId) {
        viewModel.loadCardDetail(cardId)
    }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvent.collectLatest { event ->
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

    val currentPreset = remember(uiState) {
        if (uiState is CardDetailUiState.Success) {
            val presetId = (uiState as CardDetailUiState.Success).cardWithItems?.card?.colorPresetId ?: 1
            ColorPresets.getPresetById(presetId)
        } else {
            ColorPresets.pastelBlue
        }
    }

    Scaffold(
        containerColor = currentPreset.backgroundColor.copy(alpha = 0.3f),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentPreset.backgroundColor,
                    titleContentColor = currentPreset.textColor,
                    navigationIconContentColor = currentPreset.textColor,
                    actionIconContentColor = currentPreset.textColor
                ),
                title = {
                    val cardName = (uiState as? CardDetailUiState.Success)?.cardWithItems?.card?.name ?: ""
                    Text(cardName)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (selectedItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.markSelectedItemsAsCompleted() }) {
                            Icon(Icons.Default.Check, contentDescription = "Tandai Selesai", tint = currentPreset.primaryColor)
                        }
                        IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                Text("${selectedItems.size}", color = MaterialTheme.colorScheme.onError)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Card") },
                            onClick = {
                                showMenu = false
                                showEditCardDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Card") },
                            onClick = {
                                showMenu = false
                                viewModel.onEvent(CardDetailUiEvent.DeleteCard)
                                onBackClick()
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is CardDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = currentPreset.primaryColor)
                }
            }

            is CardDetailUiState.Success -> {
                val cardWithItems = state.cardWithItems
                if (cardWithItems != null) {
                    val card = cardWithItems.card
                    val items = cardWithItems.items

                    val upcomingItems by remember(items) { mutableStateOf(items.filter { !it.isChecked }) }
                    val completedItems by remember(items) { mutableStateOf(items.filter { it.isChecked }) }
                    val progress by remember(items) {
                        mutableStateOf(if (items.isNotEmpty()) items.count { it.isChecked }.toFloat() / items.size else 0f)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        item {
                            CircularProgressCard(
                                colorPresetId = card.colorPresetId,
                                progress = progress,
                                completedCount = completedItems.size,
                                totalCount = items.size
                            )
                        }

                        item {
                            SectionHeader(title = "BELUM SELESAI", count = upcomingItems.size)
                        }

                        items(upcomingItems, key = { it.id }) { item ->
                            ReorderableItemWrapper(
                                modifier = Modifier.animateItem(
                                    placementSpec = tween(durationMillis = 400)
                                ),
                                isSelected = selectedItems.contains(item.id),
                                accentColor = currentPreset.primaryColor,
                                onTap = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.toggleItemSelection(item.id)
                                    } else {
                                        itemToEdit = item
                                        showSheet = true
                                    }
                                },
                                onLongPress = {
                                    viewModel.toggleItemSelection(item.id)
                                }
                            ) {
                                DetailChecklistItem(
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp

                                    ),
                                    itemName = item.itemName,
                                    isChecked = item.isChecked,
                                    isSelected = selectedItems.contains(item.id),
                                    deadline = item.deadline,
                                    time = item.notificationTime,
                                    hasReminder = item.isNotificationEnabled,
                                    onCheckChange = {
                                        viewModel.onEvent(CardDetailUiEvent.ChecklistItemStatusChanged(item.id, !item.isChecked))
                                    },
                                    backgroundColor = currentPreset.softBackground()
                                )
                            }
                        }

                        if (completedItems.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                SectionHeader(title = "SELESAI", count = completedItems.size)
                            }

                            items(completedItems, key = { it.id }) { item ->
                                ReorderableItemWrapper(
                                    modifier = Modifier.animateItem(
                                        placementSpec = tween(durationMillis = 400)
                                    ), // <-- API baru untuk Compose 1.7+
                                    isSelected = selectedItems.contains(item.id),
                                    accentColor = currentPreset.primaryColor,
                                    onTap = {
                                        if (selectedItems.isNotEmpty()) {
                                            viewModel.toggleItemSelection(item.id)
                                        } else {
                                            itemToEdit = item
                                            showSheet = true
                                        }
                                    },
                                    onLongPress = {
                                        viewModel.toggleItemSelection(item.id)
                                    }
                                ) {
                                    DetailChecklistItem(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                        itemName = item.itemName,
                                        isChecked = item.isChecked,
                                        isSelected = selectedItems.contains(item.id),
                                        deadline = item.deadline,
                                        time = item.notificationTime,
                                        hasReminder = item.isNotificationEnabled,
                                        onCheckChange = {
                                            viewModel.onEvent(CardDetailUiEvent.ChecklistItemStatusChanged(item.id, !item.isChecked))
                                        },
                                        backgroundColor = currentPreset.softBackground()
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                                    .clickable {
                                        itemToEdit = null
                                        showSheet = true
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = currentPreset.softBackground())
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah",
                                        tint = currentPreset.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Tambah item baru...",
                                        color = currentPreset.textColor.copy(alpha = 0.5f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("Card tidak ditemukan")
                    }
                }
            }

            is CardDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Error: ${state.message}")
                        Button(onClick = { viewModel.loadCardDetail(cardId) }) { Text("Coba Lagi") }
                    }
                }
            }
        }
    }

    // BOTTOM SHEET UNTUK ADD / EDIT ITEM TASK
    if (showSheet) {
        ItemBottomSheet(
            isEditMode = itemToEdit != null,
            initialName = itemToEdit?.itemName ?: "",
            initialDate = itemToEdit?.deadline,
            initialTime = itemToEdit?.notificationTime,
            initialReminderMinutes = if (itemToEdit?.isNotificationEnabled == true) itemToEdit?.notificationMinutesBefore else null,
            onDismiss = {
                showSheet = false
                itemToEdit = null
            },
            onSave = { itemName, deadlineDate, deadlineTime, reminderMinutes ->
                if (itemToEdit != null) {
                    viewModel.updateItemSimple(itemToEdit!!.id, itemName, deadlineDate, deadlineTime, reminderMinutes)
                } else {
                    viewModel.addNewItemSimple(itemName, deadlineDate, deadlineTime, reminderMinutes)
                }
                showSheet = false
                itemToEdit = null
            },
            buttonColor = currentPreset.primaryColor
        )
    }

    // BOTTOM SHEET UNTUK EDIT CARD UTAMA
    if (showEditCardDialog) {
        val currentCard = (uiState as? CardDetailUiState.Success)?.cardWithItems?.card
        if (currentCard != null) {
            CardDialog(
                isEditMode = true,
                initialName = currentCard.name,
                initialCategoryId = currentCard.categoryId,
                initialColorPresetId = currentCard.colorPresetId,
                categories = categories,
                onDismiss = { showEditCardDialog = false },
                onSave = { name, existingCategoryId, newCategoryName, colorPresetId ->
                    val finalCategoryId = if (!newCategoryName.isNullOrBlank()) {
                        viewModel.addCategory(newCategoryName)
                    } else {
                        existingCategoryId ?: ""
                    }
                    viewModel.updateCardDetails(cardId, name, finalCategoryId, colorPresetId)
                    showEditCardDialog = false
                },
                buttonColor = currentPreset.primaryColor
            )
        }
    }
}

/**
 * ReorderableItemWrapper: Sekarang bersih tanpa border. Hanya efek scale/elevation/shake!
 */
@Composable
fun ReorderableItemWrapper(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    accentColor: Color,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    content: @Composable () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val rotationAngle = remember { Animatable(0f) }
    val randomDelay = remember { (0..150).random() }
    val amplitude = remember { 1.5f + (0..10).random() / 10f }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            kotlinx.coroutines.delay(randomDelay.toLong())
            rotationAngle.animateTo(
                targetValue = amplitude,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = tween(100 + (0..50).random(), easing = androidx.compose.animation.core.FastOutSlowInEasing),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                )
            )
        } else {
            rotationAngle.snapTo(0f)
        }
    }

    var isPressed by remember { mutableStateOf(false) }

    val itemScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    val itemElevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = tween(150),
        label = "elevation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = if (isSelected) rotationAngle.value else 0f
                scaleX = itemScale
                scaleY = itemScale
                shadowElevation = itemElevation
            }
            .pointerInput(isSelected) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onTap() },
                    onLongPress = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                )
            }
    ) {
        content()
    }
}

@Composable
@Preview(showBackground = true)
fun CardDetailScreenRedesignPreview() {
    DonezoTheme {
        CardDetailScreen(cardId = "preview")
    }
}