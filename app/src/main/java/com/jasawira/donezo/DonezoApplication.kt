package com.jasawira.donezo

import android.app.Application
import androidx.core.content.edit
import com.jasawira.donezo.domain.repository.CategoryRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDateTime

/**
 * DonezoApplication
 * Application class untuk Donezo dengan Hilt dependency injection
 */
@HiltAndroidApp
class DonezoApplication : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize default categories jika belum ada
        initializeDefaultCategories()
    }

    private fun initializeDefaultCategories() {
        val prefs = getSharedPreferences("donezo_prefs", MODE_PRIVATE)
        val isInitialized = prefs.getBoolean("categories_initialized", false)

        if (!isInitialized) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Create default categories
                    val defaultCategories = listOf(
                        com.jasawira.donezo.domain.model.Category(
                            id = "default-work",
                            name = "Work",
                            createdAt = LocalDateTime.now()
                        ),
                        com.jasawira.donezo.domain.model.Category(
                            id = "default-personal",
                            name = "Personal",
                            createdAt = LocalDateTime.now()
                        ),
                        com.jasawira.donezo.domain.model.Category(
                            id = "default-shopping",
                            name = "Shopping",
                            createdAt = LocalDateTime.now()
                        )
                    )

                    for (category in defaultCategories) {
                        categoryRepository.addCategory(category)
                    }

                    // Mark as initialized
                    prefs.edit {
                        putBoolean("categories_initialized", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

