package fr.laforge.benoist.financialmanager.infrastructure.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.notification.BalanceNotifier

/**
 * Concrete implementation of [NotificationHelper] (domain parsing port) and
 * [BalanceNotifier] (infrastructure display port).
 *
 * Combining both in one class keeps the Android notification channel setup in a single
 * place while respecting the interface separation: callers that only need parsing depend
 * on [NotificationHelper]; callers that only need display depend on [BalanceNotifier].
 *
 * @property context Application [Context] required for notification channel creation
 *   and permission checks.
 */
class NotificationHelperImpl(private val context: Context) : NotificationHelper, BalanceNotifier {

    override fun showBalanceUpdate(newBalance: Float) {
        // ⚠️ CRITICAL: Changed ID to force Android to register new Priority settings
        val channelId = "balance_updates_high_priority"

        // Create Channel (Required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Balance Updates",
                NotificationManager.IMPORTANCE_HIGH // 🔥 REQUIRED for Heads-up
            ).apply {
                description = "Shows your remaining balance immediately"
                enableVibration(true) // 🔥 Vibration often helps trigger the visual 'pop'
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Balance Updated")
            .setContentText("New Balance: ${String.format("%.2f", newBalance)} €")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 🔥 REQUIRED for pre-Oreo & Heads-up
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // System treats messages as higher priority
            .setDefaults(NotificationCompat.DEFAULT_ALL) // 🔥 Sound/Vibrate ensures it pops
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(1001, notification)
        }
    }

    override fun isTransaction(notificationMessage: String): Result<Boolean> {
        return if (notificationMessage.contains(EURO_SYMBOL)) {
            Result.success(true)
        } else {
            Result.success(false)
        }
    }

    override fun parseNotificationMessage(notificationTitle: String, notificationMessage: String): Result<Transaction> {
        val split = notificationMessage.split(EURO_SYMBOL)

        if (split.size <= 1) {
            return Result.failure(Exception("No amount found"))
        }

        val amount = split[0].trim().replace(',', '.').toFloat()

        return if (amount < 0) {
            Result.failure(Exception("Negative amount"))
        } else {
            Result.success(
                Transaction(
                    amount = amount,
                    description = notificationTitle
                )
            )
        }
    }

    companion object {
        private const val EURO_SYMBOL = '€'
    }
}
