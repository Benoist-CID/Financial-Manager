package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Use case to get the non-recurring income for a given date.*
 */
class GetNonRecurringIncomeUseCase(private val financialRepository: FinancialRepository) {
    /**
     * Get the non-recurring income for a given date.
     *
     * @param date The date to get the income for.
     *
     * @return A Flow emitting the non-recurring income for the given date.
     */
    operator fun invoke(date: LocalDateTime = LocalDateTime.now()): Flow<Float> =
        financialRepository.getAllInDateRange(
            startDate = date.withDayOfMonth(1).toLocalDate().atStartOfDay(),
            endDate = date.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()
        ).map { transactions ->
            transactions
                .filter { it.type == TransactionType.Income && !it.isPeriodic && it.parent == 0 }
                .sumOf { it.amount.toDouble() }
                .toFloat()
        }
}
