package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository

/**
 * Receives a [ParsedNotification], deduplicates it against existing pending entries,
 * and stages the result in the [PendingTransactionRepository].
 *
 * ## Deduplication rule
 * If a [PendingTransaction] with the same [ParsedNotification.amount] already exists
 * in [DEDUPLICATION_WINDOW_MINUTES], the incoming notification is treated as a second
 * confirmation of that transaction:
 * - The new [ParsedNotification.source] is appended to the existing entry's sources list.
 * - Confidence is upgraded to [PendingTransactionConfidence.HIGH].
 * - No new row is created.
 *
 * Otherwise a new [PendingTransaction] is inserted with [PendingTransactionConfidence.LOW].
 *
 * @property repository Persistence layer for pending transactions.
 */
class ProcessIncomingNotificationUseCase(
    private val repository: PendingTransactionRepository,
) {
    /**
     * Processes [parsed] through the deduplication pipeline and stages the result.
     *
     * @param parsed The normalised notification produced by a [NotificationParser].
     */
    suspend operator fun invoke(parsed: ParsedNotification) {
        val windowStart = parsed.receivedAt.minusMinutes(DEDUPLICATION_WINDOW_MINUTES)
        val windowEnd = parsed.receivedAt

        val existing = repository.findPendingByAmountInWindow(
            amount = parsed.amount,
            from = windowStart,
            to = windowEnd,
        )

        if (existing != null) {
            // Merge: add source, upgrade confidence
            val merged = existing.copy(
                sources = (existing.sources + parsed.source).distinct(),
                confidence = PendingTransactionConfidence.HIGH,
            )
            repository.update(merged)
        } else {
            // New entry
            repository.add(
                PendingTransaction(
                    amount = parsed.amount,
                    description = parsed.description,
                    detectedAt = parsed.receivedAt,
                    sources = listOf(parsed.source),
                    confidence = PendingTransactionConfidence.LOW,
                )
            )
        }
    }

    companion object {
        /** Time window within which two notifications for the same amount are considered duplicates. */
        const val DEDUPLICATION_WINDOW_MINUTES = 2L
    }
}
