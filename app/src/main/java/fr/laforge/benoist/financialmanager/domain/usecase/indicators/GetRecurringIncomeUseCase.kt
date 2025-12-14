package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the recurring income
 */
class GetRecurringIncomeUseCase(private val financialRepository: FinancialRepository) {
    operator fun invoke(): Flow<Float> =
        financialRepository.getAllPeriodicTransactionsByType(TransactionType.Income)
            .map { transactions ->
                // Sum all amounts in the list
                transactions.map { it.amount }.sum()
            }
}
