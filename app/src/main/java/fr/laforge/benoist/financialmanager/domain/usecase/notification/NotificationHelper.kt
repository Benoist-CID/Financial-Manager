package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification

/**
 * Domain port for detecting and parsing bank/payment notifications.
 *
 * Implementations delegate to a parser registry (e.g. [NotificationParserFactory][fr.laforge.benoist.financialmanager.infrastructure.notification.NotificationParserFactory])
 * that handles multiple notification formats (Google Pay, bank proprietary, etc.).
 *
 * Display responsibilities (e.g. showing a balance-update Android notification) are
 * deliberately excluded — those are infrastructure/UI concerns and live in
 * [fr.laforge.benoist.financialmanager.infrastructure.notification.BalanceNotifier].
 */
interface NotificationHelper {

    /**
     * Returns whether [notificationMessage] represents a financial transaction
     * that can be parsed by the underlying parser registry.
     *
     * @param notificationMessage The raw text body of the status-bar notification.
     * @return [Result.success] wrapping `true` if the message looks like a transaction,
     *   `false` otherwise. [Result.failure] on unexpected errors.
     */
    fun isTransaction(notificationMessage: String): Result<Boolean>

    /**
     * Parses [notificationMessage] and [notificationTitle] into a [ParsedNotification].
     *
     * Unlike the previous [Transaction]-based parsing, this method preserves the
     * originating [fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource]
     * so that the deduplication pipeline can merge duplicate notifications.
     *
     * @param notificationTitle   The notification's title (typically the merchant name).
     * @param notificationMessage The notification's body text containing the amount.
     * @return [Result.success] wrapping the parsed [ParsedNotification], or [Result.failure]
     *   if no registered parser can handle the message.
     */
    fun parseToPending(notificationTitle: String, notificationMessage: String): Result<ParsedNotification>
}
