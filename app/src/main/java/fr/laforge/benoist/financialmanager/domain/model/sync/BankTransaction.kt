package fr.laforge.benoist.financialmanager.domain.model.sync

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import java.time.LocalDate

/**
 * A financial movement as reported by the bank via the Nordigen (GoCardless) API.
 *
 * This is a pure read-only projection of an external bank statement entry. It is never
 * persisted directly; persistence only occurs after reconciliation with an existing
 * [fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction], or
 * upon creation of a new one with [SyncStatus.NEW_FROM_BANK].
 *
 * @property bankId      The bank's unique identifier for this transaction, used to
 *                       prevent duplicate imports across successive sync calls.
 * @property amount      The absolute monetary value. Always positive; [type] determines
 *                       whether it is income or an expense from the user's perspective.
 * @property description The bank's raw label for the transaction
 *                       (e.g. "NETFLIX*123456789 PARIS").
 * @property valueDate   The date the transaction was initiated, or the date that affects
 *                       interest calculations. Used as the primary date for matching.
 * @property bookingDate The date the transaction was posted to the bank account
 *                       (settlement date).
 * @property type        Whether this movement is income or an expense for the user.
 *
 * @note The distinction between [valueDate] and [bookingDate] is critical for reconciliation:
 *   app transactions record the value date (when the user intends the money to move),
 *   while the bank may only report the booking date for older entries. All matchers
 *   compare against [valueDate].
 */
data class BankTransaction(
    val bankId: String,
    val amount: Float,
    val description: String,
    val valueDate: LocalDate,
    val bookingDate: LocalDate,
    val type: TransactionType,
)
