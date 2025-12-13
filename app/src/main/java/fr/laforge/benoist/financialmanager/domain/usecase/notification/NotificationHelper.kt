package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.Transaction

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
     * Takes a notification message and converts it to a Transaction
     */
    fun parseNotificationMessage(notificationTitle: String, notificationMessage: String): Result<Transaction>
}

class NotificationHelperImpl : NotificationHelper {
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
