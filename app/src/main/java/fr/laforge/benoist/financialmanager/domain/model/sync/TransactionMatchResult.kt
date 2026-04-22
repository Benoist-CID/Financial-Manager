package fr.laforge.benoist.financialmanager.domain.model.sync

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

/**
 * The result of a matcher pairing one app [Transaction] with one [BankTransaction].
 *
 * A single [BankTransaction] may produce multiple [TransactionMatchResult] instances when
 * several app transactions are candidates. The caller — typically a sync use case in Phase 3 —
 * is responsible for resolving ambiguities by surfacing all candidates to the user for
 * manual selection when [confidence] is not [MatchConfidence.HIGH].
 *
 * @property appTransaction  The app-side candidate transaction.
 * @property bankTransaction The bank-side transaction being reconciled.
 * @property confidence      How strongly the matcher believes these two records represent
 *                           the same financial movement.
 */
data class TransactionMatchResult(
    val appTransaction: Transaction,
    val bankTransaction: BankTransaction,
    val confidence: MatchConfidence,
)
