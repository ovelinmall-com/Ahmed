package com.ovelin.admin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

/**
 * تشتغل دايماً في الخلفية عبر النظام (مش شرط التطبيق يكون مفتوح).
 * أي إشعار يوصل من السيرفر عبر FCM يتحول فوراً لإشعار نظام
 * يظهر في شريط الحالة وستارة الإشعارات.
 */
class AdminFcmService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "admin_notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // التوكن ممكن يتغيّر أحياناً (تحديث تطبيق، إلخ) — نعيد تسجيله تلقائياً
        AdminApi.registerToken(token, onSuccess = {}, onError = {})
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "لوحة تحكم الأدمن"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "إشعارات لوحة الأدمن",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات فورية من لوحة تحكم أدمن Ovelin"
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }

        val openIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_large)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(Random.nextInt(), notification)
    }
}
