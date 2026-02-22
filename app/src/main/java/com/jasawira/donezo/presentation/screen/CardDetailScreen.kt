package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.presentation.components.*
import com.jasawira.donezo.presentation.theme.ColorPresets
import com.jasawira.donezo.presentation.uistate.CardDetailUiEvent
import com.jasawira.donezo.presentation.uistate.CardDetailUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.CardDetailViewModel
import kotlinx.coroutines.launch

/**
 * CardDetailScreenRedesign
 * Detail screen dengan design baru sesuai gambar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreenRedesign(
    modifier: Modifier = Modifier,
    cardId: String,
    viewModel: CardDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()
    val newItemName by viewModel.newItemName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showAddItemSheet by remember { mutableStateOf(false) }

    // Load card detail
    LaunchedEffect(cardId) {
        viewModel.loadCardDetail(cardId)
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
        topBar = {
            TopAppBar(
                title = {
                    val cardName = (uiState as? CardDetailUiState.Success)?.cardWithItems?.card?.name ?: ""
                    Text(cardName)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    // Show delete button when items selected
                    if (selectedItems.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.deleteSelectedItems()
                        }) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error
                            ) {
                                Text("${selectedItems.size}")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    }

                    // Dropdown menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Card") },
                            onClick = {
                                showMenu = false
                                // TODO: Open edit card dialog
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CardDetailUiState.Success -> {
                val cardWithItems = state.cardWithItems
                if (cardWithItems != null) {
                    val card = cardWithItems.card
                    val items = cardWithItems.items
                    val colorPreset = ColorPresets.getPresetById(card.colorPresetId)

                    // Split items by checked status
                    val upcomingItems = items.filter { !it.isChecked }
                    val completedItems = items.filter { it.isChecked }
                    val progress = if (items.isNotEmpty()) items.count { it.isChecked }.toFloat() / items.size else 0f

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Progress Card
                        item {
                            CircularProgressCard(
                                progress = progress,
                                backgroundColor = colorPreset.backgroundColor,
                                progressColor = colorPreset.primaryColor,
                                completedCount = completedItems.size,
                                totalCount = items.size
                            )
                        }

                        // Upcoming Tasks Section
                        item {
                            SectionHeader(
                                title = "BELUM SELESAI",
                                count = upcomingItems.size
                            )
                        }

                        // Upcoming items
                        items(upcomingItems, key = { it.id }) { item ->
                            DetailChecklistItem(
                                itemName = item.itemName,
                                isChecked = item.isChecked,
                                isSelected = selectedItems.contains(item.id),
                                onCheckChange = {
                                    viewModel.onEvent(
                                        CardDetailUiEvent.ChecklistItemStatusChanged(item.id, !item.isChecked)
                                    )
                                },
                                onItemClick = {
                                    if (selectedItems.isNotEmpty() || selectedItems.contains(item.id)) {
                                        viewModel.toggleItemSelection(item.id)
                                    }
                                },
                                backgroundColor = Color.White
                            )
                        }

                        // Completed Section
                        if (completedItems.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                SectionHeader(
                                    title = "SELESAI",
                                    count = completedItems.size
                                )
                            }

                            items(completedItems, key = { it.id }) { item ->
                                DetailChecklistItem(
                                    itemName = item.itemName,
                                    isChecked = item.isChecked,
                                    isSelected = selectedItems.contains(item.id),
                                    onCheckChange = {
                                        viewModel.onEvent(
                                            CardDetailUiEvent.ChecklistItemStatusChanged(item.id, !item.isChecked)
                                        )
                                    },
                                    onItemClick = {
                                        if (selectedItems.isNotEmpty() || selectedItems.contains(item.id)) {
                                            viewModel.toggleItemSelection(item.id)
                                        }
                                    },
                                    backgroundColor = colorPreset.backgroundColor.copy(alpha = 0.3f)
                                )
                            }
                        }

                        // Inline Add Item - Click to open bottom sheet
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                                    .clickable { showAddItemSheet = true },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah",
                                        tint = colorPreset.primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Tambah item baru...",
                                        color = Color.Gray.copy(alpha = 0.5f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                } else {
                    // No data
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Card tidak ditemukan")
                    }
                }
            }

            is CardDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Error: ${state.message}")
                        Button(onClick = { viewModel.loadCardDetail(cardId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }

    // Add Item Bottom Sheet
    if (showAddItemSheet) {
        val categories = (uiState as? CardDetailUiState.Success)?.cardWithItems?.let { cardWithItems ->
            // Get all categories - TODO: fetch from viewmodel
            emptyList<Category>()
        } ?: emptyList()

        AddItemBottomSheet(
            categories = categories,
            onDismiss = { showAddItemSheet = false },
            onAddItem = { itemName, deadline, notificationTime, isNotificationEnabled, minutesBefore, categoryId ->
                // TODO: Handle new category creation if needed
                viewModel.addNewItemWithDetails(
                    itemName = itemName,
                    deadline = deadline,
                    notificationTime = notificationTime,
                    isNotificationEnabled = isNotificationEnabled,
                    notificationMinutesBefore = minutesBefore
                )
            },
            buttonColor = (uiState as? CardDetailUiState.Success)?.cardWithItems?.let { cardWithItems ->
                ColorPresets.getPresetById(cardWithItems.card.colorPresetId).primaryColor
            } ?: Color(0xFF26D3C8)
        )
    }
}


