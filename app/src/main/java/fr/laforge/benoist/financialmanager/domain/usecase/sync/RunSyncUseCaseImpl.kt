package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.repository.SyncSettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Production implementation of [RunSyncUseCase].
 *
 * Execution steps:
 * 1. Filter the supplied [bankTransactions] to entries whose [BankTransaction.valueDate]
 *    falls within [dateFrom]–[dateTo] (the CSV export may cover a wider period).
 * 2. Load all app transactions in the same range from [financialRepository], then keep only
 *    [SyncStatus.PENDING] entries (already-synced rows are ignored).
 * 3. For each bank transaction, run [StandardTransactionMatcher] (exact amount, ±3 days)
 *    against manual entries, and [RecurringTransactionMatcher] (fuzzy amount, ±15 days)
 *    against recurring children.
 * 4. Flatten all match results and return them.
 *
 * @property financialRepository   Source of app-side transaction history.
 * @property standardMatcher       Matcher for manually-entered transactions.
 * @property recurringMatcher      Matcher for recurring-child transactions.
 * @property syncSettingsRepository User-configurable tolerance settings.
 *
 * @note A single bank transaction may appear in the result list multiple times if several
 *   app transactions are candidates. The UI layer is responsible for presenting all
 *   candidates and letting the user resolve ambiguity.
 * @note Date filtering is performed on [BankTransaction.valueDate] because that is the date
 *   used by all matchers as the primary comparison date.
 */
class RunSyncUseCaseImpl(
    private val financialRepository: FinancialRepository,
    private val standardMatcher: StandardTransactionMatcher,
    private val recurringMatcher: RecurringTransactionMatcher,
    private val syncSettingsRepository: SyncSettingsRepository,
) : RunSyncUseCase {

    override suspend fun invoke(
        bankTransactions: List<BankTransaction>,
        dateFrom: LocalDate,
        dateTo: LocalDate,
    ): Result<List<TransactionMatchResult>> = runCatching {
        // The imported CSV may span several months; keep only the requested window.
        val filteredBankTransactions = bankTransactions.filter { bankTx ->
            !bankTx.valueDate.isBefore(dateFrom) && !bankTx.valueDate.isAfter(dateTo)
        }

        val pendingAppTransactions = financialRepository
            .getAllInDateRange(
                startDate = dateFrom.atStartOfDay(),
                endDate = dateTo.atTime(23, 59, 59),
            )
            .first()
            .filter { it.syncStatus == SyncStatus.PENDING }

        val settings = syncSettingsRepository.get().first()

        filteredBankTransactions.flatMap { bankTx ->
            standardMatcher.match(pendingAppTransactions, bankTx) +
                recurringMatcher.match(pendingAppTransactions, bankTx, settings)
        }
    }
}
