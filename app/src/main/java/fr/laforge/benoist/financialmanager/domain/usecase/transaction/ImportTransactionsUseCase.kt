package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.util.transactionFromCsv
import timber.log.Timber

/**
 * Use case responsible for importing a batch of transactions from a raw CSV string.
 *
 * Centralising this logic in the domain layer ensures that the ViewModel remains a
 * thin coordinator of UI state, free from parsing concerns and direct repository access.
 *
 * The CSV format expected per line is defined by [fr.laforge.benoist.financialmanager.domain.util.exportToCsvFormat]:
 * `uid;dateTimeMillis;amount;description;type;isPeriodic;period;parent;category`
 *
 * @property createTransactionUseCase Domain use case that persists a single transaction.
 *
 * @note Lines that cannot be parsed are silently skipped — this mirrors the original
 * ViewModel behaviour and is intentional: a partial import is better than a full failure
 * when dealing with user-supplied CSV data that may contain blank lines or encoding artefacts.
 */
class ImportTransactionsUseCase(
    private val createTransactionUseCase: CreateTransactionUseCase,
) {

    /**
     * Parses [csv] line-by-line and persists each valid transaction.
     *
     * @param csv Raw CSV string where each line represents one transaction.
     * @return The number of transactions successfully imported.
     *
     * @note Malformed lines are caught and logged at warning level, then skipped.
     * The caller receives the count of successfully persisted rows so it can surface
     * a meaningful result to the user if needed.
     */
    suspend operator fun invoke(csv: String): Int {
        var imported = 0
        csv.split('\n').forEach { line ->
            try {
                val transaction = transactionFromCsv(line)
                createTransactionUseCase(transaction)
                imported++
            } catch (e: Exception) {
                Timber.w(e, "Skipping malformed CSV line: $line")
            }
        }
        return imported
    }
}
