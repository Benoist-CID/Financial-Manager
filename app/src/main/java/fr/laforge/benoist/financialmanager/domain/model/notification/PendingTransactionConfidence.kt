package fr.laforge.benoist.financialmanager.domain.model.notification

/**
 * Confidence level of a [PendingTransaction] based on how many independent
 * notification sources confirmed the same payment.
 */
enum class PendingTransactionConfidence {
    /** Only one notification source detected this transaction. Requires user validation. */
    LOW,

    /** Two independent sources (e.g. Google Pay + Bank) matched the same amount within
     *  the deduplication window. High reliability — user validation still offered. */
    HIGH,
}
