package fr.laforge.benoist.financialmanager.infrastructure.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRemainingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.CreateTransactionFromNotificationUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.notification.BalanceNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.Collections

class NotificationListener : NotificationListenerService() {
    private val createTransactionFromNotificationUseCase: CreateTransactionFromNotificationUseCase by inject()
    private val getRemainingBalanceUseCase: GetRemainingBalanceUseCase by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val balanceNotifier: BalanceNotifier by inject()
    private val notificationListenerHelper: NotificationListenerHelper by inject()


    // 1. Keep track of processed notifications to avoid updates triggering duplicates
    private val processedNotifications =  Collections.synchronizedSet(mutableSetOf<String>())

    // Use a SupervisorJob for the service scope
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        // Safety checks
        if (sbn == null) return
        if (sbn.packageName == this.packageName) return // Don't parse our own notifications

        // Ignores group summary
        if (notificationListenerHelper.isGroupSummary(sbn)) {
            Timber.d("Ignoring group summary notification from ${sbn.packageName}")
            return
        }

        // --- FIX 2: PREVENT DUPLICATE PROCESSING (Updates) ---
        // Use a unique key. sbn.key includes pkg, id, and tag.
        // We append postTime to allow re-processing if a NEW notification reuses the ID (rare but possible)
        // OR just use sbn.key if you are sure IDs are unique per transaction.
        val uniqueKey = "${sbn.key}_${sbn.postTime}"

        if (processedNotifications.contains(uniqueKey)) {
            Timber.d("Notification already processed: $uniqueKey")
            return
        }

        val title = sbn.notification.extras.getString("android.title") ?: "No description"
        val text = sbn.notification.extras.getString("android.text")

        Timber.i("New notification: $title")

        text?.let { message ->
            serviceScope.launch {
                // Try to create transaction
                val isSuccess = createTransactionFromNotificationUseCase(
                    notificationTitle = title,
                    notificationMessage = message
                )

                if (isSuccess) {
                    // Mark as processed ONLY if it was a valid transaction
                    processedNotifications.add(uniqueKey)

                    val newBalance = getRemainingBalanceUseCase().first()
                    withContext(Dispatchers.Main) {
                        balanceNotifier.showBalanceUpdate(newBalance)
                    }
                }
            }
        } ?: Timber.e("Notification message is null")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
