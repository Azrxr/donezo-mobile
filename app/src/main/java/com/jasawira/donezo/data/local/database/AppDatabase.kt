package com.jasawira.donezo.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jasawira.donezo.data.local.dao.CardDao
import com.jasawira.donezo.data.local.dao.CategoryDao
import com.jasawira.donezo.data.local.dao.ChecklistItemDao
import com.jasawira.donezo.data.local.entity.CardEntity
import com.jasawira.donezo.data.local.entity.CategoryEntity
import com.jasawira.donezo.data.local.entity.ChecklistItemEntity
import com.jasawira.donezo.utils.DateTimeConverters

/**
 * Room Database Configuration
 *
 * Entities:
 * - CategoryEntity: Menyimpan kategori
 * - CardEntity: Menyimpan card project
 * - ChecklistItemEntity: Menyimpan checklist items
 *
 * Version: 1
 * - Bisa di-increment jika ada migration
 *
 * TypeConverters: Untuk handle LocalDateTime, LocalDate, LocalTime
 */
@Database(
    entities = [
        CategoryEntity::class,
        CardEntity::class,
        ChecklistItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    // Abstract getter untuk setiap DAO
    abstract fun categoryDao(): CategoryDao
    abstract fun cardDao(): CardDao
    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        private const val DATABASE_NAME = "checklist_app.db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Singleton pattern untuk AppDatabase
         * Thread-safe dengan double-checked locking
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: createDatabase(context).also { instance = it }
            }
        }

        private fun createDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // Untuk development
                .build()
        }

        /**
         * Function untuk reset database (jika user minta "Hapus semua data")
         */
        fun resetDatabase(context: Context) {
            synchronized(this) {
                instance?.close()
                context.deleteDatabase(DATABASE_NAME)
                instance = null
            }
        }
    }
}

