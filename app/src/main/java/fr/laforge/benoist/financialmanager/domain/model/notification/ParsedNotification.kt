package fr.laforge.benoist.financialmanager.domain.model.notification

import java.time.LocalDateTime

/**
 * Normalised representation of a financial notification after parsing.
 *
 * This value object is produced by any [fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationParser]
 * implementation and serves as the common language between the infrastructure
 * parsing layer and the domain deduplication logic.
 *
 * @property amount      Payment amount in euros.
 * @property description Merchant or payment description.
 * @property source      The notification system that produced this entry.
 * @property receivedAt  Timestamp when the notification was received.
 */
data class ParsedNotification(
    val amount: Float,
    val description: String,
    val source: NotificationSource,
    val receivedAt: LocalDateTime = LocalDateTime.now(),
)
