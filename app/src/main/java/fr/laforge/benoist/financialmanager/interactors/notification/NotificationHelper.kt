package fr.laforge.benoist.financialmanager.interactors.notification

import fr.laforge.benoist.financialmanager.util.NotificationUtil
import fr.laforge.benoist.model.Notification
import fr.laforge.benoist.model.Transaction

interface NotificationHelper {
    /**
     * Indicates if a message is a transaction or not
     *
     * @param notification Notification
     *
     * @return Result<Boolean>
     */
    fun isTransaction(notification: Notification): Result<Boolean>

    /**
     * Takes a notification message and converts it to a Transaction
     */
    fun parseNotificationMessage(notificationMessage: String): Result<Transaction>

    /**
     * Creates a notification to create a new transaction
     */
    fun postNotification(notification: Notification)
}

class NotificationHelperImpl(private val notificationUtil: NotificationUtil) : NotificationHelper {
    override fun isTransaction(notification: Notification): Result<Boolean> {
        return if (notification.message.contains(EURO_SYMBOL)) {
            Result.success(true)
        } else {
            Result.success(false)
        }
    }

    override fun parseNotificationMessage(notificationMessage: String): Result<Transaction> {
        val split = notificationMessage.split(EURO_SYMBOL)

        if (split.size <= 1) {
            // Error
        }

        return Result.success(
            Transaction(
                amount = split[1].replace(',', '.').toFloat(),
                description = split[0].trim()
            )
        )
    }

    override fun postNotification(notification: Notification) {
        notificationUtil.createNotification(notification = notification)
    }

    companion object {
        private const val EURO_SYMBOL = '€'
    }
}
