package fr.laforge.benoist.financialmanager.service

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import fr.laforge.benoist.financialmanager.usecase.notification.CreateTransactionFromNotificationUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class NotificationListener : NotificationListenerService() {
    private val createTransactionFromNotificationUseCase: CreateTransactionFromNotificationUseCase by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn?.notification?.extras?.getString("android.text")?. let {
            MainScope().launch {
                createTransactionFromNotificationUseCase(notificationMessage = it)
            }
        } ?: Timber.e("Notification message is null")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
