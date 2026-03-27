package fr.laforge.benoist.financialmanager.domain.model.notification

import java.time.LocalDateTime
import java.util.UUID

/**
 * A financial transaction detected from a notification but not yet validated by the user.
 *
 * A [PendingTransaction] is created whenever the notification pipeline detects a payment.
 * Multiple notifications for the same payment (e.g. one from Google Pay and one from the
 * bank app) are merged into a single entry whose [confidence] is upgraded to [PendingTransactionConfidence.HIGH].
 *
 * The entry remains [PendingTransactionStatus.PENDING] until the user either confirms it
 * (promoting it to the main transaction history) or dismisses it (discarding it as a
 * false positive).
 *
 * @property id          Unique identifier for this pending entry.
 * @property amount      Detected payment amount in euros.
 * @property description Merchant or payment description extracted from the notification.
 * @property detectedAt  Timestamp of the first notification that created this entry.
 * @property sources     All notification sources that contributed to this entry.
 * @property confidence  Reliability level based on the number of confirming sources.
 * @property status      Current lifecycle state of this entry.
 */
data class PendingTransaction(
    val id: UUID = UUID.randomUUID(),
    val amount: Float,
    val description: String,
    val detectedAt: LocalDateTime = LocalDateTime.now(),
    val sources: List<NotificationSource>,
    val confidence: PendingTransactionConfidence = PendingTransactionConfidence.LOW,
    val status: PendingTransactionStatus = PendingTransactionStatus.PENDING,
)
