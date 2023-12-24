package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface CreateRegularTransactionsUseCase {
    suspend fun execute(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currentDate: LocalDateTime = LocalDateTime.now(),
        type: TransactionType = TransactionType.Expense
    ): Flow<Boolean>
}
