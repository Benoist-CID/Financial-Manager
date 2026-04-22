package fr.laforge.benoist.financialmanager.domain.model.transaction

import java.time.LocalDateTime

/**
 * Projection of a [Transaction] representing a single expense scheduled between
 * now and the end of the current month.
 *
 * Intentionally narrower than [Transaction]: omits sync state, periodicity
 * configuration, and parent FK, which are irrelevant to forward-looking planning.
 *
 * @property uid         Unique identifier matching the source [Transaction.uid].
 * @property date        The scheduled date and time of the expense.
 * @property amount      The monetary value; always positive.
 * @property description Human-readable label (e.g. "Netflix", "Electricity").
 * @property category    The budget category this expense belongs to.
 * @property isRecurring `true` when this expense is a generated instance of a
 *                       periodic template (i.e. source [Transaction.parent] != 0).
 */
data class UpcomingExpense(
    val uid: Int,
    val date: LocalDateTime,
    val amount: Float,
    val description: String,
    val category: TransactionCategory,
    val isRecurring: Boolean,
)
