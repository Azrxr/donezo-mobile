package com.jasawira.donezo.notification

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

/**
 * ALARM RECEIVER
 * BroadcastReceiver yang menangani trigger alarm dari AlarmManager
 */
class AlarmReceiver : android.content.BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.checklistapp.ALARM_TRIGGER") {
            val itemId = intent.getStringExtra("itemId") ?: return
            val cardName = intent.getStringExtra("cardName") ?: return
            val itemName = intent.getStringExtra("itemName") ?: return
            val isReminder = intent.getBooleanExtra("isReminder", true)

            val notificationManager = NotificationManager(context)
            notificationManager.showNotification(cardName, itemName, isReminder)
        }
    }
}