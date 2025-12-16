package fr.laforge.benoist.financialmanager.infrastructure.service

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRemainingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.CreateTransactionFromNotificationUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.getValue

class NotificationListener : NotificationListenerService() {
    private val createTransactionFromNotificationUseCase: CreateTransactionFromNotificationUseCase by inject()
    private val getRemainingBalanceUseCase: GetRemainingBalanceUseCase by inject() // To fetch the new total
    private val notificationHelper: NotificationHelper by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val title = sbn?.notification?.extras?.getString("android.title") ?: "No description"

        sbn?.notification?.extras?.getString("android.text")?.let {
            MainScope().launch {
                if (createTransactionFromNotificationUseCase(
                        notificationTitle = title,
                        notificationMessage = it
                    )
                ) {
                    // We fetch the "Fresh" balance immediately after the insert
                    val newBalance = getRemainingBalanceUseCase().first()

                    notificationHelper.showBalanceUpdate(newBalance)
                }
            }
        } ?: Timber.e("Notification message is null")
    }
}
