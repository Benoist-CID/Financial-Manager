package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

/**
 * Domain port for parsing bank/payment notifications into [Transaction] objects.
 *
 * This interface contains only the two pure parsing operations that constitute a
 * domain concern: deciding whether a notification text represents a transaction, and
 * extracting a [Transaction] from the raw notification strings.
 *
 * Display responsibilities (e.g. showing a balance-update Android notification) are
 * deliberately excluded — those are infrastructure/UI concerns and live in
 * [fr.laforge.benoist.financialmanager.infrastructure.notification.BalanceNotifier].
 */
interface NotificationHelper {

    /**
     * Returns whether [notificationMessage] represents a financial transaction.
     *
     * @param notificationMessage The raw text body of the status-bar notification.
     * @return [Result.success] wrapping `true` if the message looks like a transaction,
     *   `false` otherwise. [Result.failure] on unexpected parse errors.
     */
    fun isTransaction(notificationMessage: String): Result<Boolean>

    /**
     * Parses [notificationMessage] and [notificationTitle] into a [Transaction].
     *
     * @param notificationTitle  The notification's title (typically the merchant name).
     * @param notificationMessage The notification's body text containing the amount.
     * @return [Result.success] wrapping the parsed [Transaction], or [Result.failure]
     *   if the amount could not be extracted or is negative.
     */
    fun parseNotificationMessage(notificationTitle: String, notificationMessage: String): Result<Transaction>
}
