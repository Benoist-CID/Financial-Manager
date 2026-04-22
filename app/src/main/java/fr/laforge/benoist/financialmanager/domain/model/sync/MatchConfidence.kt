package fr.laforge.benoist.financialmanager.domain.model.sync

/**
 * The confidence level of a match between an app transaction and a [BankTransaction],
 * as produced by a transaction matcher.
 *
 * This type is intentionally distinct from
 * [fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence]
 * to keep the sync domain decoupled from the notification domain. The two concepts
 * exist for different purposes and are expected to evolve independently.
 *
 * @note [LOW] and [MEDIUM] confidence always require explicit user confirmation before the
 *   reconciliation is applied. [HIGH] confidence matches may be auto-confirmed in a
 *   future enhancement, but that behaviour is out of scope for this epic.
 */
enum class MatchConfidence {
    /**
     * Weak signal: only one criterion (e.g. amount alone) matched.
     * Always requires user confirmation.
     */
    LOW,

    /**
     * Moderate signal: amount and date matched, but description did not.
     * Always requires user confirmation.
     */
    MEDIUM,

    /**
     * Strong signal: amount, date, and description all matched.
     */
    HIGH,
}
