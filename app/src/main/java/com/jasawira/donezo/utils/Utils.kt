package com.jasawira.donezo.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * CONSTANTS
 */
object AppConstants {
    const val APP_NAME = "Donezo - Checklist Manager"
    const val NOTIFICATION_CHANNEL_ID = "checklist_notifications"
    const val NOTIFICATION_CHANNEL_NAME = "Checklist Reminders"

    // Default values
    const val DEFAULT_NOTIFICATION_MINUTES_BEFORE = 30
    const val MAX_CHECKLIST_ITEMS = 999
    const val MAX_CARD_NAME_LENGTH = 100
    const val MAX_ITEM_NAME_LENGTH = 150
    const val MAX_CATEGORY_NAME_LENGTH = 50

    // Notification IDs
    const val NOTIFICATION_BASE_ID = 1000
    const val SNOOZE_DURATION_MINUTES = 10

    // Drag Drop
    const val DRAG_DROP_REORDER_THRESHOLD = 10 // dp
}

/**
 * DATETIME UTILITIES
 */
object DateTimeUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

    /**
     * Format LocalDate to String
     * Example: "22 Feb 2024"
     */
    fun formatDate(date: LocalDate?): String {
        return date?.format(dateFormatter) ?: "-"
    }

    /**
     * Format LocalTime to String
     * Example: "14:30"
     */
    fun formatTime(time: LocalTime?): String {
        return time?.format(timeFormatter) ?: "-"
    }

    /**
     * Format LocalDateTime to String
     * Example: "22 Feb 2024, 14:30"
     */
    fun formatDateTime(dateTime: LocalDateTime?): String {
        return dateTime?.format(dateTimeFormatter) ?: "-"
    }

    /**
     * Check if deadline sudah passed
     */
    fun isDeadlineOverdue(date: LocalDate): Boolean {
        return date.isBefore(LocalDate.now())
    }

    /**
     * Check if deadline hari ini
     */
    fun isDeadlineToday(date: LocalDate): Boolean {
        return date.isEqual(LocalDate.now())
    }

    /**
     * Check if deadline besok
     */
    fun isDeadlineTomorrow(date: LocalDate): Boolean {
        return date.isEqual(LocalDate.now().plusDays(1))
    }

    /**
     * Get days remaining hingga deadline
     */
    fun getDaysRemaining(date: LocalDate): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), date)
    }

    /**
     * Get deadline status text (Overdue, Today, Tomorrow, X days, dll)
     */
    fun getDeadlineStatusText(date: LocalDate): String {
        return when {
            isDeadlineOverdue(date) -> "Sudah lewat"
            isDeadlineToday(date) -> "Hari ini"
            isDeadlineTomorrow(date) -> "Besok"
            else -> {
                val days = getDaysRemaining(date)
                "$days hari lagi"
            }
        }
    }

    /**
     * Parse String ke LocalDate
     */
    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse String ke LocalTime
     */
    fun parseTime(timeString: String): LocalTime? {
        return try {
            LocalTime.parse(timeString, timeFormatter)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * UUID GENERATOR
 */
object UuidGenerator {
    fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    fun generateId(prefix: String): String {
        return "$prefix-${UUID.randomUUID()}"
    }
}

/**
 * EXTENSION FUNCTIONS
 */

/**
 * Check apakah string tidak kosong dan tidak blank
 */
fun String?.isNotEmptyOrBlank(): Boolean {
    return !this.isNullOrBlank()
}

/**
 * Truncate string jika melebihi max length
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength - 3) + "..."
    } else {
        this
    }
}

/**
 * Extension untuk Float progress (0-1) to percentage (0-100)
 */
fun Float.toPercentage(): Int {
    return (this * 100).toInt().coerceIn(0, 100)
}

/**
 * Extension untuk format progress
 */
fun Pair<Int, Int>.formatProgress(): String {
    val (completed, total) = this
    val percentage = if (total == 0) 0 else (completed * 100 / total)
    return "$completed/$total ($percentage%)"
}

/**
 * Extension untuk check jika list kosong dengan action
 */
inline fun <T> List<T>.ifEmpty(action: () -> Unit) {
    if (this.isEmpty()) {
        action()
    }
}

/**
 * Extension untuk debug logging
 */
fun <T> T.debugLog(tag: String = "DEBUG", message: String = ""): T {
    android.util.Log.d(tag, "$message $this")
    return this
}

/**
 * Extension untuk error logging
 */
fun <T> T.errorLog(tag: String = "ERROR", throwable: Throwable? = null): T {
    android.util.Log.e(tag, this.toString(), throwable)
    return this
}

