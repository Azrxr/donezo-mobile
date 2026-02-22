package com.jasawira.donezo.notification

import android.Manifest
import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jasawira.donezo.MainActivity
import com.jasawira.donezo.utils.AppConstants
import kotlin.random.Random


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    /**
     * Tampilkan notifikasi reminder
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
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
        return notificationId
    }

    /**
     * Cancel notifikasi
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
