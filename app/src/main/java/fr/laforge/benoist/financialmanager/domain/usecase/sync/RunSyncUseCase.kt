package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import java.time.LocalDate

/**
 * Domain use case that orchestrates a full bank-sync run for a given date range.
 *
 * The caller is responsible for obtaining the [bankTransactions] list (e.g. by parsing a
 * bank-exported CSV via [ParseCsvBankTransactionsUseCase]). This use case then filters the
 * supplied transactions to [dateFrom]–[dateTo], loads all
 * [fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus.PENDING] app transactions
 * for the same period, and runs both [StandardTransactionMatcher] and [RecurringTransactionMatcher]
 * to produce a consolidated list of candidates for the user to review.
 *
 * @note Sync is triggered manually by the user (no background/WorkManager scheduling).
 * @note The CSV export from the bank may cover a wider date range than the current month.
 *   Client-side date filtering is applied inside the implementation.
 */
interface RunSyncUseCase {

    /**
     * Executes the sync and returns all match candidates found.
     *
     * @param bankTransactions The full list of bank transactions parsed from the imported CSV.
     *   Entries outside [dateFrom]–[dateTo] are filtered out internally.
     * @param dateFrom Inclusive start of the date range to match against app transactions.
     * @param dateTo   Inclusive end of the date range to match against app transactions.
     * @return [Result.success] wrapping a (possibly empty) list of [TransactionMatchResult],
     *   or [Result.failure] if loading app transactions fails.
     */
    suspend operator fun invoke(
        bankTransactions: List<BankTransaction>,
        dateFrom: LocalDate,
        dateTo: LocalDate,
    ): Result<List<TransactionMatchResult>>
}
