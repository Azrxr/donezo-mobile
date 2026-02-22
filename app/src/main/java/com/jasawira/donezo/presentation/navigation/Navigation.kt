package com.jasawira.donezo.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jasawira.donezo.presentation.screen.HomeScreen
import com.jasawira.donezo.presentation.screen.CardDetailScreen

/**
 * Navigation Routes
 */
object NavigationRoutes {
    const val HOME = "home"
    const val CARD_DETAIL = "card_detail/{cardId}"
    const val MANAGE_CATEGORY = "manage_category"
    const val SETTINGS = "settings"

    fun cardDetailRoute(cardId: String) = "card_detail/$cardId"
}

/**
 * Navigation Graph untuk Donezo Checklist Manager
 */
@Composable
fun ChecklistAppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOME
    ) {
        // HOME SCREEN
        composable(NavigationRoutes.HOME) {
            HomeScreen(
                onCardClick = { cardId ->
                    navController.navigate(NavigationRoutes.cardDetailRoute(cardId))
                },
                onNavigateToCategory = {
                    navController.navigate(NavigationRoutes.MANAGE_CATEGORY)
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationRoutes.SETTINGS)
                }
            )
        }

        // CARD DETAIL SCREEN
        composable(NavigationRoutes.CARD_DETAIL) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
            CardDetailScreen(
                cardId = cardId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // MANAGE CATEGORIES PLACEHOLDER
        composable(NavigationRoutes.MANAGE_CATEGORY) {
            ManageCategoryPlaceholder(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // SETTINGS PLACEHOLDER
        composable(NavigationRoutes.SETTINGS) {
            SettingsPlaceholder(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Placeholder untuk Manage Categories Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoryPlaceholder(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Manage Categories - Coming Soon")
        }
    }
}

/**
 * Placeholder untuk Settings Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPlaceholder(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Settings - Coming Soon")
        }
    }
}


