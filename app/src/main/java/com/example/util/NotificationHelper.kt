package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

data class InAppNotification(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType // PAYMENT, OVERDUE, CONTRACT, MAINTENANCE
)

enum class NotificationType {
    PAYMENT, OVERDUE, CONTRACT, MAINTENANCE
}

object NotificationHelper {

    private const val CHANNEL_PAYMENTS = "channel_payments"
    private const val CHANNEL_ALERTS = "channel_alerts"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val paymentChannel = NotificationChannel(
                CHANNEL_PAYMENTS,
                "Kemaskini Pembayaran Masa Nyata",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pemberitahuan tolak bagi transaksi pembayaran sewa yang disahkan"
            }

            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Peringatan Tertunggak & Kontrak",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pemberitahuan peringatan sewa tertunggak dan tempoh tamat kontrak"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(paymentChannel)
            manager?.createNotificationChannel(alertChannel)
        }
    }

    fun sendPushNotification(
        context: Context,
        title: String,
        message: String,
        isAlert: Boolean = false,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // If not granted, still safe, returns
                return
            }
        }

        try {
            val channelId = if (isAlert) CHANNEL_ALERTS else CHANNEL_PAYMENTS
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.urussewa_icon_1793231580379)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(notificationId, builder.build())
        } catch (_: Exception) {
            // Ignore if notification cannot be delivered
        }
    }
}
