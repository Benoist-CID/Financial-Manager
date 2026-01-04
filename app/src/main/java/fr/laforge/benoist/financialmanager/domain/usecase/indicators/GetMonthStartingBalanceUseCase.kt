package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * A use case to retrieve the current balance at beginning of the month
 */
class GetMonthStartingBalanceUseCase(
    private val repository: FinancialRepository
) {
    /**
     * Calculates the balance as it was at the very beginning of the requested month.
     *
     * @param month The month to get the balance for.
     *
     * @return A flow of the balance.
     */
    operator fun invoke(month: LocalDateTime): Flow<Float> {
        // We want the sum of everything BEFORE the 1st of this month at 00:00
        val startOfMonth = month.withDayOfMonth(1)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        repository.getTransactionsBeforeDate(startOfMonth)
        return repository.getBalanceBeforeDate(startOfMonth)
    }
}