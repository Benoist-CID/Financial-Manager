package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecurringExpenseTemplatesUseCase(private val repository: FinancialRepository) {
    operator fun invoke(): Flow<List<Transaction>> =
        repository.getAll().map { transactions ->
            transactions
                .filter { it.isPeriodic && it.type == TransactionType.Expense }
                .sortedByDescending { it.amount } // Show biggest expenses first
        }
}
