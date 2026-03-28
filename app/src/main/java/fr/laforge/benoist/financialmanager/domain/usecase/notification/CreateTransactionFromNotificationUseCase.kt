package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case that stages an incoming notification as a
 * [fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction]
 * for later user confirmation or dismissal.
 *
 * Orchestrates notification detection, parsing, and deduplication by delegating to
 * [NotificationHelper] and [ProcessIncomingNotificationUseCase].
 * Returns `true` when the notification was successfully staged, `false` for any non-fatal
 * failure (unrecognised notification, parse error).
 *
 * @property processIncomingNotificationUseCase Stages the parsed notification with deduplication.
 * @property notificationHelper Detects and parses raw notification strings.
 * @property dispatcher Coroutine dispatcher for the parse/persist work.
 *   Defaults to [Dispatchers.IO]; override in tests for determinism.
 * @property logger Domain [Logger] for diagnostic output. Defaults to [Logger.NoOp].
 */
class CreateTransactionFromNotificationUseCase(
    private val processIncomingNotificationUseCase: ProcessIncomingNotificationUseCase,
    private val notificationHelper: NotificationHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val logger: Logger = Logger.NoOp,
) {
    /**
     * Attempts to stage a pending transaction from the given notification strings.
     *
     * @param notificationTitle   The notification's title (typically the app or merchant name).
     * @param notificationMessage The notification's body text, expected to contain a monetary amount.
     * @return `true` if a pending transaction was successfully staged;
     *   `false` if the message was not a transaction or parsing failed.
     */
    suspend operator fun invoke(notificationTitle: String, notificationMessage: String): Boolean =
        withContext(dispatcher) {
            // 1. Guard: decide whether this notification contains a transaction
            val isTransaction = notificationHelper.isTransaction(notificationMessage)
                .getOrDefault(false)

            if (!isTransaction) {
                logger.error("Not a valid transaction")
                return@withContext false
            }

            // 2. Parse into a ParsedNotification (preserves source for deduplication)
            val parsedResult = notificationHelper.parseToPending(
                notificationTitle = notificationTitle,
                notificationMessage = notificationMessage,
            )

            val parsed = parsedResult.getOrNull()
            if (parsed == null) {
                logger.error(
                    message = "Failed to parse notification",
                    throwable = parsedResult.exceptionOrNull(),
                )
                return@withContext false
            }

            // 3. Stage the parsed notification (deduplication handled inside)
            processIncomingNotificationUseCase(parsed)
            true
        }
}
