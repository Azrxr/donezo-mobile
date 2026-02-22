package com.jasawira.donezo.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jasawira.donezo.MainActivity
import com.jasawira.donezo.utils.AppConstants
import kotlin.random.Random
import android.Manifest
import androidx.annotation.RequiresPermission


/**
 * NOTIFICATION MANAGER
 * Mengelola pembuatan dan penampilan notifikasi
 */
class NotificationManager(private val context: Context) {

    init {
        createNotificationChannel()
    }

    /**
     * Buat notification channel untuk Android 8+
     */
    private fun createNotificationChannel() {
        val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
        val channel = android.app.NotificationChannel(
            AppConstants.NOTIFICATION_CHANNEL_ID,
            AppConstants.NOTIFICATION_CHANNEL_NAME,
            importance
        ).apply {
            description = "Notifikasi reminder untuk checklist items"
            enableLights(true)
            enableVibration(true)
            setShowBadge(true)
        }

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Tampilkan notifikasi reminder
     * Dipanggil saat alarm trigger
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        cardName: String,
        itemName: String,
        isReminder: Boolean = true
    ): Int {
        val notificationId = Random.nextInt(100000)

        val title = cardName
        val message = if (isReminder) {
            "$itemName - dimulai dalam 30 menit"
        } else {
            "$itemName - waktunya sekarang!"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(
            context,
            AppConstants.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(com.jasawira.donezo.R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
        return notificationId
    }

    /**
     * Cancel notifikasi
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    /**
     * Cancel all notifications
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}

