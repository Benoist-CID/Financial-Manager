package fr.laforge.benoist.financialmanager.service

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import fr.laforge.benoist.financialmanager.interactors.notification.CreateTransactionFromNotificationInteractor
import fr.laforge.benoist.financialmanager.usecase.notification.CreateTransactionNotificationUseCase
import fr.laforge.benoist.model.Notification
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class NotificationListener : NotificationListenerService() {
    private val createTransactionFromNotificationUseCase: CreateTransactionFromNotificationInteractor by inject()
    private val createTransactionNotificationUseCase: CreateTransactionNotificationUseCase by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val title = sbn?.notification?.extras?.getString("android.title") ?: ""
        val text = sbn?.notification?.extras?.getString("android.text") ?: ""
        if (title != "Financial Manager") {
            sbn?.notification?.extras?.getString("android.text")?.let {
                MainScope().launch {
                    createTransactionFromNotificationUseCase(
                        notification = Notification(
                            title = title,
                            message = text
                        )
                    )
                }
            } ?: Timber.e("Notification message is null")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

        val title = sbn?.notification?.extras?.getString("android.title") ?: ""
        val text = sbn?.notification?.extras?.getString("android.text") ?: ""

        if (title != "Financial Manager") {
            createTransactionNotificationUseCase.invoke(
                notification = Notification(
                    title = title,
                    message = text
                )
            )
        }
    }
}
