package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository

/**
 * Creates a new [Transaction] from a [BankTransaction] that had no matching app entry.
 *
 * The resulting transaction is created with [SyncStatus.NEW_FROM_BANK] and
 * [TransactionCategory.None] so the user can review and categorise it later.
 *
 * Use this when the user explicitly dismisses all match candidates for a bank entry
 * but still wants the transaction recorded in the app.
 *
 * @property financialRepository Repository used to persist the new transaction.
 *
 * @note [BankTransaction.valueDate] is used as the transaction's datetime (at midnight)
 *   because it best reflects when the user's balance was affected.
 */
class CreateTransactionFromBankUseCase(private val financialRepository: FinancialRepository) {

    /**
     * Creates and persists a new [Transaction] from [bankTransaction].
     *
     * @param bankTransaction The bank statement entry to import.
     * @return The database row ID of the newly created transaction.
     */
    operator fun invoke(bankTransaction: BankTransaction): Long {
        val transaction = Transaction(
            dateTime = bankTransaction.valueDate.atStartOfDay(),
            amount = bankTransaction.amount,
            description = bankTransaction.description,
            type = bankTransaction.type,
            syncStatus = SyncStatus.NEW_FROM_BANK,
            category = TransactionCategory.None,
        )
        return financialRepository.createTransaction(transaction)
    }
}
