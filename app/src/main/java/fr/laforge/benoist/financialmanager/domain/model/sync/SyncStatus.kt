package fr.laforge.benoist.financialmanager.domain.model.sync

/**
 * Represents the reconciliation state of a [fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction]
 * relative to the bank account statement.
 *
 * This enum is the single source of truth for sync state. The previously considered
 * `isEffective` flag was removed in favour of this richer model.
 *
 * @note State transitions follow this lifecycle:
 *   - All new app transactions start as [PENDING].
 *   - A sync run matches a [PENDING] transaction to a bank entry → [IN_SYNC].
 *   - A bank entry with no matching app transaction → auto-created as [NEW_FROM_BANK].
 */
enum class SyncStatus {
    /**
     * The transaction has been entered in the app but has not yet appeared in the bank statement.
     * This is the default state for all newly created transactions.
     */
    PENDING,

    /**
     * The transaction has been matched and confirmed against a corresponding entry
     * in the bank statement.
     */
    IN_SYNC,

    /**
     * The transaction was discovered in the bank statement and automatically created
     * by the sync process. It had no prior entry in the app.
     * The user should review and categorise it; default category is [fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory.None].
     */
    NEW_FROM_BANK,
}
