package fr.laforge.benoist.financialmanager.domain.model.notification

/**
 * Lifecycle status of a [PendingTransaction] in the validation queue.
 */
enum class PendingTransactionStatus {
    /** Awaiting user action. */
    PENDING,

    /** User confirmed — promoted to the main transaction history. */
    CONFIRMED,

    /** User dismissed — treated as a false positive and discarded. */
    DISMISSED,
}
