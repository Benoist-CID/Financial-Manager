package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.TransactionType
import java.time.LocalDateTime

interface CreateRegularTransactionsUseCase {
    suspend fun execute(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currentDate: LocalDateTime = LocalDateTime.now(),
        type: TransactionType = TransactionType.Expense
    ): Boolean
}
