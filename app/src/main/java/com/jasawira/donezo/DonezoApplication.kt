package com.jasawira.donezo

import android.app.Application
import androidx.core.content.edit
import com.jasawira.donezo.domain.model.Card
import com.jasawira.donezo.domain.model.Category
import com.jasawira.donezo.domain.model.ChecklistItem
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.domain.repository.ChecklistRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * DonezoApplication
 * Application class untuk Donezo dengan Hilt dependency injection
 */
@HiltAndroidApp
class DonezoApplication : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var cardRepository: CardRepository

    @Inject
    lateinit var checklistRepository: ChecklistRepository

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi data bawaan (Onboarding) jika ini adalah fresh install
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        val prefs = getSharedPreferences("donezo_prefs", MODE_PRIVATE)

        // Gunakan key baru agar user yang sudah pernah install (tapi cuma ada kategori)
        // tetap mendapatkan Card Welcome saat aplikasi di-update
        val isInitialized = prefs.getBoolean("is_onboarding_completed", false)

        if (!isInitialized) {
            // Gunakan Dispatchers.IO agar tidak memblokir Main Thread (UI) saat startup
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 1. BUAT KATEGORI DEFAULT
                    val uncategorizedId = UUID.randomUUID().toString()
                    val personalId = UUID.randomUUID().toString()
                    val workId = UUID.randomUUID().toString()

                    val defaultCategories = listOf(
                        Category(id = uncategorizedId, name = "Uncategorized", createdAt = LocalDateTime.now()),
                        Category(id = personalId, name = "Pribadi", createdAt = LocalDateTime.now()),
                        Category(id = workId, name = "Pekerjaan", createdAt = LocalDateTime.now())
                    )

                    for (category in defaultCategories) {
                        categoryRepository.addCategory(category)
                    }

                    // 2. BUAT CARD WELCOME (TUGAS PERDANA)
                    val welcomeCardId = UUID.randomUUID().toString()
                    val welcomeCard = Card(
                        id = welcomeCardId,
                        name = "Selamat Datang di Donezo! 🎉",
                        categoryId = personalId, // Dimasukkan ke kategori Pribadi
                        colorPresetId = 0, // Warna tema biru (preset pertama)
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                    cardRepository.addCard(welcomeCard)

                    // 3. BUAT CHECKLIST ITEMS SEBAGAI TUTORIAL
                    val items = listOf(
                        ChecklistItem(
                            id = UUID.randomUUID().toString(),
                            cardId = welcomeCardId,
                            itemName = "Buat tugas pertamamu dengan menekan tombol + di bawah",
                            isChecked = true, // Sengaja dicentang agar progress terlihat
                            createdAt = LocalDateTime.now()
                        ),
                        ChecklistItem(
                            id = UUID.randomUUID().toString(),
                            cardId = welcomeCardId,
                            itemName = "Tahan (long-press) card di Beranda untuk mengedit atau menghapus",
                            isChecked = false,
                            createdAt = LocalDateTime.now()
                        ),
                        ChecklistItem(
                            id = UUID.randomUUID().toString(),
                            cardId = welcomeCardId,
                            itemName = "Buka menu Pengaturan (Roda Gigi) untuk kelola daftar Kategori",
                            isChecked = false,
                            createdAt = LocalDateTime.now()
                        )
                    )

                    // Simpan semua item ke database
                    for (item in items) {
                        checklistRepository.addItem(item)
                    }

                    // TANDAI SELESAI: Agar data tidak dibuat ganda saat aplikasi dibuka lagi besok
                    prefs.edit {
                        putBoolean("is_onboarding_completed", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}