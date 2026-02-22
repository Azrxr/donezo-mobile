package com.jasawira.donezo.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.model.FilterOptions
import com.jasawira.donezo.presentation.components.CardComponent
import com.jasawira.donezo.presentation.components.EmptyStateComponent
import com.jasawira.donezo.presentation.components.SkeletonLoadingList
import com.jasawira.donezo.presentation.theme.DonezoTheme
import com.jasawira.donezo.presentation.uistate.HomeUiEvent
import com.jasawira.donezo.presentation.uistate.HomeUiState
import com.jasawira.donezo.presentation.uistate.SnackbarEvent
import com.jasawira.donezo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * HomeScreen
 * Main screen yang menampilkan list of cards
 *
 * Features:
 * - Search cards by name
 * - Filter by category & status
 * - Drag-drop reorder cards
 * - CRUD operations
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddCardSheet by remember { mutableStateOf(false) }
    var selectedCardForMenu by remember { mutableStateOf<Card?>(null) }

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
            HomeTopBar(
                onMenuClick = onMenuClick,
                onSearchChange = { query ->
                    viewModel.onEvent(HomeUiEvent.SearchCards(query))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCardSheet = true },
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
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
                is HomeUiState.Loading -> {
                    SkeletonLoadingList(count = 3)
                }

                is HomeUiState.Success -> {
                    val successState = uiState as HomeUiState.Success
                    val cards = successState.filteredCards
                    val categories = successState.categories

                    if (cards.isEmpty()) {
                        EmptyStateComponent(
                            icon = "📭",
                            title = "Belum ada card",
                            subtitle = "Mulai dengan membuat card baru!",
                            onCreateClick = { showAddCardSheet = true },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Filter chips
                            item {
                                FilterChips(
                                    categories = categories,
                                    currentFilter = successState.filterOptions,
                                    onFilterChange = { categoryId ->
                                        viewModel.onEvent(
                                            HomeUiEvent.FilterByCategory(categoryId)
                                        )
                                    },
                                    onClearFilter = {
                                        viewModel.onEvent(HomeUiEvent.ClearFilter)
                                    }
                                )
                            }

                            // Cards list
                            items(
                                items = cards,
                                key = { it.id }
                            ) { card ->
                                CardComponent(
                                    cardName = card.name,
                                    categoryName = categories.find { it.id == card.categoryId }?.name ?: "",
                                    colorPresetId = card.colorPresetId,
                                    progress = card.progress,
                                    itemCount = card.itemCount,
                                    completedCount = card.completedItemCount,
                                    onCardClick = {
                                        onCardClick(card.id)
                                    },
                                    onMenuClick = {
                                        selectedCardForMenu = card
                                    }
                                )
                            }
                        }
                    }
                }

                is HomeUiState.Error -> {
                    val errorState = uiState as HomeUiState.Error
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
                                viewModel.onEvent(HomeUiEvent.RefreshCards)
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
    if (showAddCardSheet) {
        AddCardBottomSheet(
            categories = (uiState as? HomeUiState.Success)?.categories ?: emptyList(),
            onDismiss = { showAddCardSheet = false },
            onAddCard = { name, categoryId, colorId ->
                viewModel.addCard(name, categoryId, colorId)
                showAddCardSheet = false
            }
        )
    }

    // Card menu
    if (selectedCardForMenu != null) {
        CardMenuDialog(
            card = selectedCardForMenu!!,
            onEdit = {
                // Navigate to edit screen
                selectedCardForMenu = null
            },
            onDelete = {
                viewModel.onEvent(HomeUiEvent.DeleteCard(selectedCardForMenu!!.id))
                selectedCardForMenu = null
            },
            onDismiss = {
                selectedCardForMenu = null
            }
        )
    }
}

/**
 * HomeTopBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit = {},
    onSearchChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    if (isSearchExpanded) {
        TopAppBar(
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                        onSearchChange(newQuery)
                    },
                    placeholder = { Text("Search cards...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = { isSearchExpanded = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = "Checklist Manager",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                // Search icon - expand ke search bar
                IconButton(onClick = { isSearchExpanded = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            modifier = modifier
        )
    }
}

/**
 * FilterChips
 */
@Composable
fun FilterChips(
    categories: List<Category>,
    currentFilter: FilterOptions,
    onFilterChange: (String?) -> Unit = {},
    onClearFilter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (currentFilter.categoryId != null || currentFilter.searchQuery.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter aktif:",
                    style = MaterialTheme.typography.labelSmall
                )

                if (currentFilter.categoryId != null) {
                    AssistChip(
                        onClick = { onFilterChange(null) },
                        label = {
                            Text(
                                categories.find { it.id == currentFilter.categoryId }?.name ?: "Unknown"
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = onClearFilter) {
                    Text("Clear")
                }
            }
        }
    }
}

/**
 * CardMenuDialog
 */
@Composable
fun CardMenuDialog(
    card: Card,
    onDelete: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilihan") },
        text = { Text(card.name) },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                    onDismiss()
                }
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * AddCardBottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheet(
    categories: List<Category>,
    onDismiss: () -> Unit = {},
    onAddCard: (name: String, categoryId: String, colorId: Int) -> Unit = { _, _, _ -> }
) {
    var cardName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var selectedColorId by remember { mutableStateOf(0) }

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
//                            .menuAnchor(type = MenuAnchorType.PrimaryEdge, enabled = true)
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
                            onAddCard(cardName, selectedCategoryId, selectedColorId)
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    DonezoTheme {
        HomeScreen()
    }
}
