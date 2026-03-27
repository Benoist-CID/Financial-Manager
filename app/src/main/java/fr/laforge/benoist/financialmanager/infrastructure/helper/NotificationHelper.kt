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
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.notification.BalanceNotifier
import fr.laforge.benoist.financialmanager.infrastructure.notification.NotificationParserFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

/**
 * Concrete implementation of [NotificationHelper] (domain parsing port) and
 * [BalanceNotifier] (infrastructure display port).
 *
 * Parsing is fully delegated to [factory], which selects the correct parser at runtime
 * (Google Pay format, bank proprietary format, user-defined formats, etc.).
 *
 * User-defined formats are kept fresh via a background coroutine that collects
 * [NotificationFormatRepository.getAll] and stores the latest snapshot in an
 * [AtomicReference]. The [factory]'s `userFormatsProvider` lambda reads from that
 * reference on every invocation, so newly added formats are picked up without restarting
 * the service.
 *
 * @property context          Application [Context] required for notification channel setup.
 * @property formatRepository Source of user-defined [NotificationFormat] entries.
 * @property factory          Parser registry. Injected for testability; defaults to a new
 *   [NotificationParserFactory] that reads from [_cachedFormats].
 */
class NotificationHelperImpl(
    private val context: Context,
    private val formatRepository: NotificationFormatRepository,
    private val factory: NotificationParserFactory = NotificationParserFactory(
        userFormatsProvider = { emptyList() } // overwritten after init
    ),
) : NotificationHelper, BalanceNotifier {

    private val _cachedFormats = AtomicReference<List<NotificationFormat>>(emptyList())

    private val _factory: NotificationParserFactory = NotificationParserFactory(
        userFormatsProvider = { _cachedFormats.get() }
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            formatRepository.getAll().collect { formats ->
                _cachedFormats.set(formats)
            }
        }
    }

    override fun showBalanceUpdate(newBalance: Float) {
        val channelId = "balance_updates_high_priority"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Balance Updates",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Shows your remaining balance immediately"
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Balance Updated")
            .setContentText("New Balance: ${String.format("%.2f", newBalance)} €")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1001, notification)
        }
    }

    override fun isTransaction(notificationMessage: String): Result<Boolean> =
        Result.success(_factory.canParse(notificationMessage))

    override fun parseToPending(
        notificationTitle: String,
        notificationMessage: String,
    ): Result<ParsedNotification> = _factory.parse(notificationTitle, notificationMessage)
}
