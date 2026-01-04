package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.util.getFirstDayOfMonth
import fr.laforge.benoist.financialmanager.domain.util.getLastDayOfMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetRegularExpensesUseCase(
    private val financialRepository: FinancialRepository,
) {
    operator fun invoke(): Flow<Float> = financialRepository.getTransactions(
        TransactionFilter(
            type = TransactionType.Expense,
            startDate = LocalDateTime.now().getFirstDayOfMonth(),
            endDate = LocalDateTime.now().getLastDayOfMonth().plusDays(1),
            descriptionQuery = "",
            isPeriodic = false,
            parentId = 0
        )
    ).map { transactions ->
        transactions
            .sumOf { it.amount.toDouble() }
            .toFloat()
    }
}
