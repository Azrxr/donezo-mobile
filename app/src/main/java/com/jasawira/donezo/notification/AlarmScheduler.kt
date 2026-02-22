package com.jasawira.donezo.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * ALARM SCHEDULER
 * Mengatur alarm untuk trigger notifikasi pada waktu yang ditentukan
 */
class AlarmScheduler @Inject constructor(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedule dua notifikasi untuk satu item:
     * 1. X menit sebelum deadline
     * 2. Pada waktu deadline
     */
    fun scheduleItemNotifications(
        itemId: String,
        cardName: String,
        itemName: String,
        deadline: LocalDateTime,
        minutesBefore: Int = 30
    ) {
        // Notifikasi 1: X menit sebelum deadline
        val reminderTime = deadline.minusMinutes(minutesBefore.toLong())
        scheduleAlarm(
            itemId = itemId,
            triggerTime = reminderTime,
            cardName = cardName,
            itemName = itemName,
            isReminder = true
        )

        // Notifikasi 2: Saat deadline
        scheduleAlarm(
            itemId = itemId,
            triggerTime = deadline,
            cardName = cardName,
            itemName = itemName,
            isReminder = false
        )
    }

    /**
     * Schedule single alarm
     */
    private fun scheduleAlarm(
        itemId: String,
        triggerTime: LocalDateTime,
        cardName: String,
        itemName: String,
        isReminder: Boolean
    ) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.checklistapp.ALARM_TRIGGER"
            putExtra("itemId", itemId)
            putExtra("cardName", cardName)
            putExtra("itemName", itemName)
            putExtra("isReminder", isReminder)
        }

        val alarmId = (itemId + (if (isReminder) "0" else "1")).hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTimeMs = triggerTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Jangan set alarm di masa lalu
        if (System.currentTimeMillis() < triggerTimeMs) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeMs,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeMs,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                // Handle permission error
                e.printStackTrace()
            }
        }
    }

    /**
     * Cancel notifikasi untuk item
     */
    fun cancelItemNotifications(itemId: String) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java)

        // Cancel notifikasi reminder
        val reminderId = (itemId + "0").hashCode()
        val reminderPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (reminderPendingIntent != null) {
            alarmManager.cancel(reminderPendingIntent)
            reminderPendingIntent.cancel()
        }

        // Cancel notifikasi deadline
        val deadlineId = (itemId + "1").hashCode()
        val deadlinePendingIntent = PendingIntent.getBroadcast(
            context,
            deadlineId,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (deadlinePendingIntent != null) {
            alarmManager.cancel(deadlinePendingIntent)
            deadlinePendingIntent.cancel()
        }
    }

    /**
     * Reschedule notifikasi jika deadline berubah
     */
    fun rescheduleItemNotifications(
        itemId: String,
        cardName: String,
        itemName: String,
        newDeadline: LocalDateTime,
        minutesBefore: Int = 30
    ) {
        // Cancel yang lama
        cancelItemNotifications(itemId)

        // Schedule yang baru
        scheduleItemNotifications(
            itemId = itemId,
            cardName = cardName,
            itemName = itemName,
            deadline = newDeadline,
            minutesBefore = minutesBefore
        )
    }
}