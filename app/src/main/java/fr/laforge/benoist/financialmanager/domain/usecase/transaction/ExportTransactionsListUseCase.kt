package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.util.exportToCsvFormat

/**
 * Use case responsible for converting a list of [Transaction] into a CSV string.
 *
 * This represents the business rule for data serialization for export purposes.
 */
class ExportTransactionsListUseCase {

    /**
     * Converts a list of transactions into a CSV formatted string.
     *
     * @param transactions The list of transactions to export.
     * @return A [Result] containing the CSV string on success, or an error if the list is empty.
     */
    operator fun invoke(transactions: List<Transaction>): Result<String> {
        if (transactions.isEmpty()) {
            return Result.failure(IllegalArgumentException("No transactions to export"))
        }

        return try {
            val csvBuilder = StringBuilder()
            transactions.forEach { transaction ->
                csvBuilder.append(transaction.exportToCsvFormat()).append("\n")
            }
            Result.success(csvBuilder.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
