package fr.laforge.benoist.financialmanager.usecase.notification

import fr.laforge.benoist.model.Transaction

interface NotificationHelper {
    /**
     * Indicates if a message is a transaction or not
     *
     * @param notificationMessage Notification message
     *
     * @return Result<Boolean>
     */
    fun isTransaction(notificationMessage: String): Result<Boolean>

    /**
     * TAkes a notification message and converts it to a Transaction
     */
    fun parseNotificationMessage(notificationMessage: String): Result<Transaction>
}

class NotificationHelperImpl : NotificationHelper {
    override fun isTransaction(notificationMessage: String): Result<Boolean> {
        return if (notificationMessage.contains(EURO_SYMBOL)) {
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

    companion object {
        private const val EURO_SYMBOL = '€'
    }
}
