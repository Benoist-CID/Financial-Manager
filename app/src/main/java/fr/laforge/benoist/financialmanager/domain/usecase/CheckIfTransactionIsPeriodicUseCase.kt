package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

/**
 * Determines whether a [Transaction] is a child of a recurring template.
 *
 * A transaction is considered "periodic" (i.e. generated rather than manual) when
 * its [Transaction.parent] field is non-zero, meaning it was automatically created
 * from a recurring template.
 */
interface CheckIfTransactionIsPeriodicUseCase {
    /**
     * @param transaction The transaction to inspect.
     * @return `true` if [transaction] was generated from a recurring template
     *   ([Transaction.parent] != 0); `false` for manual entries.
     */
    operator fun invoke(transaction: Transaction): Boolean
}

/**
 * Default implementation of [CheckIfTransactionIsPeriodicUseCase].
 *
 * @note The check is purely structural: a non-zero [Transaction.parent] is the
 * canonical signal that this row was auto-generated, regardless of
 * [Transaction.isPeriodic] (which flags templates, not child instances).
 */
class CheckIfTransactionIsPeriodicUseCaseImpl : CheckIfTransactionIsPeriodicUseCase {
    override fun invoke(transaction: Transaction): Boolean {
        return transaction.parent != 0
    }
}
