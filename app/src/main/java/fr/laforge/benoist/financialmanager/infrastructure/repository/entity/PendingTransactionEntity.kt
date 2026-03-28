package fr.laforge.benoist.financialmanager.infrastructure.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room entity representing a [PendingTransaction] in the local database.
 *
 * [id] is stored as a [String] because Room has no native UUID column type.
 * The [sources] list is serialised by [fr.laforge.benoist.financialmanager.infrastructure.repository.converters.NotificationSourceConverters].
 */
@Entity(tableName = "pending_transaction")
data class PendingTransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "detected_at") val detectedAt: LocalDateTime,
    @ColumnInfo(name = "sources") val sources: List<NotificationSource>,
    @ColumnInfo(name = "confidence") val confidence: PendingTransactionConfidence,
    @ColumnInfo(name = "status") val status: PendingTransactionStatus,
) {
    /** Converts this entity to its domain representation. */
    fun toModel(): PendingTransaction = PendingTransaction(
        id = UUID.fromString(id),
        amount = amount,
        description = description,
        detectedAt = detectedAt,
        sources = sources,
        confidence = confidence,
        status = status,
    )

    companion object {
        /** Creates a [PendingTransactionEntity] from a domain [PendingTransaction]. */
        fun fromModel(model: PendingTransaction) = PendingTransactionEntity(
            id = model.id.toString(),
            amount = model.amount,
            description = model.description,
            detectedAt = model.detectedAt,
            sources = model.sources,
            confidence = model.confidence,
            status = model.status,
        )
    }
}
