package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.util.getFirstDayOfMonth
import fr.laforge.benoist.financialmanager.domain.util.getLastDayOfMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetRegularExpensesUseCase(
    private val financialRepository: FinancialRepository,
) {
    operator fun invoke(): Flow<Float> =
        financialRepository.getAllInDateRange(
            startDate = LocalDateTime.now().getFirstDayOfMonth(),
            endDate = LocalDateTime.now().getLastDayOfMonth().plusDays(1)
        ).map { transactions ->
            transactions
                .filter { it.type == TransactionType.Expense && !it.isPeriodic && it.parent == 0 }
                .sumOf { it.amount.toDouble() }
                .toFloat()
        }
}
