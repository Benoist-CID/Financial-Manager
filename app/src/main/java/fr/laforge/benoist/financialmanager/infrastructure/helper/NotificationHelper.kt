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
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.notification.BalanceNotifier
import fr.laforge.benoist.financialmanager.infrastructure.notification.NotificationParserFactory

/**
 * Concrete implementation of [NotificationHelper] (domain parsing port) and
 * [BalanceNotifier] (infrastructure display port).
 *
 * Parsing is fully delegated to [factory], which selects the correct parser at runtime
 * (Google Pay format, bank proprietary format, etc.) and returns a [ParsedNotification]
 * that preserves the originating source for the deduplication pipeline.
 *
 * @property context Application [Context] required for notification channel creation
 *   and permission checks.
 * @property factory Parser registry for all supported notification formats.
 *   Defaults to a [NotificationParserFactory] with the standard set of parsers; can be
 *   overridden in tests to inject a controlled factory.
 */
class NotificationHelperImpl(
    private val context: Context,
    private val factory: NotificationParserFactory = NotificationParserFactory(),
) : NotificationHelper, BalanceNotifier {

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

    override fun isTransaction(notificationMessage: String): Result<Boolean> =
        Result.success(factory.canParse(notificationMessage))

    override fun parseToPending(
        notificationTitle: String,
        notificationMessage: String,
    ): Result<ParsedNotification> = factory.parse(notificationTitle, notificationMessage)
}
