package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import java.time.LocalDateTime

/**
 * Application-service port for generating child transaction instances from periodic templates.
 *
 * Called once per period (typically on app launch) to ensure that every recurring
 * template has a corresponding concrete child transaction inside the current date window.
 * If a child already exists for a given template in the window, no duplicate is created.
 */
interface CreateRegularTransactionsUseCase {
    /**
     * Iterates all periodic templates and creates one child instance per template
     * that falls inside [[startDate], [endDate]] and has no existing child yet.
     *
     * @param startDate   The inclusive start of the target period.
     * @param endDate     The inclusive end of the target period.
     * @param currentDate The reference date used to anchor the child's day-of-month.
     *   Defaults to [LocalDateTime.now].
     * @param type        Restricts generation to templates of a specific [TransactionType].
     *   Defaults to [TransactionType.Expense].
     * @return `true` when the operation completes (even if no child was created).
     */
    suspend fun execute(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currentDate: LocalDateTime = LocalDateTime.now(),
        type: TransactionType = TransactionType.Expense
    ): Boolean
}
