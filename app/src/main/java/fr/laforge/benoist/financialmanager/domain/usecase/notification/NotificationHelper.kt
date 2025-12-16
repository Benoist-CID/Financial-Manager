package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

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

    fun showBalanceUpdate(newBalance: Float)
}
