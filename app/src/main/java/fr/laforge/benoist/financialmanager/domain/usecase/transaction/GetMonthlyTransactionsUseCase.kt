package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

class GetMonthlyTransactionsUseCase(
    private val financialRepository: FinancialRepository
) {
    operator fun invoke(
        month: YearMonth,
        searchQuery: String = "",
        filterType: TransactionType? = null
    ): Flow<List<Transaction>> {

        // 1. Business Logic: Define the precise time window
        val startDate = month.atDay(1).atStartOfDay() // 00:00:00
        val endDate = month.atEndOfMonth().atTime(23, 59, 59) // 23:59:59

        // 2. Business Logic: Configure the Filter
        // We want actual transactions (isPeriodic = false), not Templates.
        // We want ALL transactions (parentId = null), both manual and recurring instances.
        val filter = TransactionFilter(
            type = filterType,
            startDate = startDate,
            endDate = endDate,
            descriptionQuery = searchQuery,
            isPeriodic = false,
            parentId = null
        )

        // 3. specific sorting or post-processing could happen here if not in SQL
        return financialRepository.getTransactions(filter)
    }
}
