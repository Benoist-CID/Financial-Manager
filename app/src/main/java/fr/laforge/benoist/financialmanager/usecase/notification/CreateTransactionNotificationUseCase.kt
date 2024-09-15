package fr.laforge.benoist.financialmanager.usecase.notification

import fr.laforge.benoist.financialmanager.interactors.notification.NotificationHelper
import fr.laforge.benoist.model.Notification

/**
 * Creates a notification when a transaction notification is removed
 */
interface CreateTransactionNotificationUseCase {
    operator fun invoke(notification: Notification)
}

class CreateTransactionNotificationUseCaseImpl(
    private val notificationHelper: NotificationHelper
) :
    CreateTransactionNotificationUseCase {
    override fun invoke(notification: Notification) {
        if (notificationHelper.isTransaction(notification = notification) == Result.success(
                true
            )
        ) {
            notificationHelper.postNotification(notification = Notification(title = "Financial Manager", message = notification.message))
        }
    }
}
