package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.domain.model.CardWithChecklistItems
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.presentation.components.CardComponent
import com.jasawira.donezo.presentation.components.ChecklistItemComponent
import com.jasawira.donezo.presentation.components.ProgressBarWithPercentage
import com.jasawira.donezo.presentation.components.SkeletonLoadingCard
import com.jasawira.donezo.presentation.theme.ColorPresets
import com.jasawira.donezo.presentation.uistate.CardDetailUiEvent
import com.jasawira.donezo.presentation.uistate.CardDetailUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.CardDetailViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/**
 * CardDetailScreen
 * Menampilkan detail card dengan semua items yang bisa di-check, diedit, dihapus
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    cardId: String,
    viewModel: CardDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddItemSheet by remember { mutableStateOf(false) }
    var showEditCardSheet by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ChecklistItem?>(null) }
    var itemToEdit by remember { mutableStateOf<ChecklistItem?>(null) }

    // Load card detail
    LaunchedEffect(cardId) {
        viewModel.loadCardDetail(cardId)
    }

    // Listen to snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { event ->
            scope.launch {
                when (event) {
                    is SnackbarEvent.Success -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    is SnackbarEvent.Error -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    is SnackbarEvent.Info -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    else -> {}
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Card") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddItemSheet = true },
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                is CardDetailUiState.Loading -> {
                    SkeletonLoadingCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                is CardDetailUiState.Success -> {
                    val successState = uiState as CardDetailUiState.Success
                    val cardWithItems = successState.cardWithItems

                    if (cardWithItems != null) {
                        val card = cardWithItems.card
                        val items = cardWithItems.items
                        val colorPreset = ColorPresets.getPresetById(card.colorPresetId)
                        val itemCount = items.size
                        val completedCount = items.count { it.isChecked }
                        val progress = if (itemCount > 0) completedCount.toFloat() / itemCount else 0f

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Card Header dengan info
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorPreset.backgroundColor
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Title + Edit button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = card.name,
                                                style = MaterialTheme.typography.titleLarge,
                                                color = colorPreset.textColor,
                                                modifier = Modifier.weight(1f)
                                            )

                                            IconButton(
                                                onClick = { showEditCardSheet = true },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = colorPreset.accentColor
                                                )
                                            }
                                        }

                                        // Progress bar
                                        ProgressBarWithPercentage(
                                            progress = progress,
                                            progressPercentage = (progress * 100).toInt(),
                                            color = colorPreset.primaryColor,
                                            textColor = colorPreset.textColor
                                        )

                                        // Item count info
                                        Text(
                                            text = "$completedCount dari $itemCount item selesai",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorPreset.textColor.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            // Items list
                            if (items.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Belum ada item. Tambahkan item baru!",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            } else {
                                items(
                                    items = items,
                                    key = { it.id }
                                ) { item ->
                                    ChecklistItemComponent(
                                        itemName = item.itemName,
                                        isChecked = item.isChecked,
                                        deadline = if (item.deadline != null) {
                                            "${item.deadline} @ ${item.notificationTime}"
                                        } else null,
                                        hasNotification = item.isNotificationEnabled,
                                        onCheckChanged = { isChecked ->
                                            viewModel.onEvent(
                                                CardDetailUiEvent.ChecklistItemStatusChanged(
                                                    item.id,
                                                    isChecked
                                                )
                                            )
                                        },
                                        onDelete = {
                                            itemToDelete = item
                                        },
                                        colorPreset = colorPreset
                                    )
                                }
                            }

                            // Spacer for FAB
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }

                is CardDetailUiState.Error -> {
                    val errorState = uiState as CardDetailUiState.Error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "❌ Error",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = errorState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.onEvent(CardDetailUiEvent.RefreshCardDetail)
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }

    // Bottom sheets
    if (showAddItemSheet) {
        AddItemBottomSheet(
            onDismiss = { showAddItemSheet = false },
            onAddItem = { itemName, deadline, notificationTime, notificationMinutes, isNotificationEnabled ->
                viewModel.onEvent(
                    CardDetailUiEvent.AddItem(
                        ChecklistItem(
                            id = java.util.UUID.randomUUID().toString(),
                            cardId = cardId,
                            itemName = itemName,
                            deadline = deadline,
                            notificationTime = notificationTime,
                            notificationMinutesBefore = notificationMinutes,
                            isNotificationEnabled = isNotificationEnabled,
                            createdAt = java.time.LocalDateTime.now()
                        )
                    )
                )
                showAddItemSheet = false
            }
        )
    }

    if (showEditCardSheet) {
        EditCardBottomSheet(
            onDismiss = { showEditCardSheet = false }
        )
    }

    // Delete confirmation
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Item?") },
            text = { Text("Yakin ingin menghapus '${itemToDelete!!.itemName}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(CardDetailUiEvent.DeleteItem(itemToDelete!!.id))
                        itemToDelete = null
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

/**
 * AddItemBottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    onDismiss: () -> Unit = {},
    onAddItem: (
        itemName: String,
        deadline: LocalDate?,
        notificationTime: LocalTime?,
        notificationMinutes: Int,
        isNotificationEnabled: Boolean
    ) -> Unit = { _, _, _, _, _ -> }
) {
    var itemName by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf<LocalDate?>(null) }
    var notificationTime by remember { mutableStateOf<LocalTime?>(null) }
    var notificationMinutes by remember { mutableStateOf(30) }
    var isNotificationEnabled by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tambah Item", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Nama Item") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // Date picker
            Button(
                onClick = {
                    // Show date picker dialog
                    deadline = LocalDate.now().plusDays(1)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(deadline?.toString() ?: "Pilih Deadline")
            }

            // Time picker
            if (deadline != null) {
                Button(
                    onClick = {
                        // Show time picker dialog
                        notificationTime = LocalTime.of(14, 0)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(notificationTime?.toString() ?: "Pilih Waktu")
                }
            }

            // Enable notification toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Aktifkan Notifikasi")
                Switch(
                    checked = isNotificationEnabled && deadline != null,
                    onCheckedChange = { isNotificationEnabled = it && deadline != null }
                )
            }

            // Notification time spinner
            if (isNotificationEnabled && deadline != null) {
                OutlinedButton(
                    onClick = { /* Expand dropdown */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("$notificationMinutes menit sebelumnya")
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            onAddItem(itemName, deadline, notificationTime, notificationMinutes, isNotificationEnabled)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Tambah")
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

/**
 * EditCardBottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardBottomSheet(
    onDismiss: () -> Unit = {}
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Edit Card", style = MaterialTheme.typography.titleLarge)

            Text("Feature untuk edit card (name, color, category)", style = MaterialTheme.typography.bodySmall)

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("Tutup")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
