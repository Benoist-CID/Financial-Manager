package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.Transaction
import java.time.LocalDateTime

interface GetCurrentMonthTransactionsUseCase {
    fun execute(currentDateTime: LocalDateTime): List<Transaction>
}
