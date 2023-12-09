package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.util.getFirstDayOfMonth
import fr.laforge.benoist.financialmanager.util.getLastDayOfMonth
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

class GetCurrentMonthTransactionsUseCaseImpl : GetCurrentMonthTransactionsUseCase, KoinComponent {
    private val repository : FinancialRepository by inject()

    override fun execute(currentDateTime: LocalDateTime): List<Transaction> {
//        val transactions = repository.getAllInDateRange(
//            startDate = currentDateTime.getFirstDayOfMonth(),
//            endDate = currentDateTime.getLastDayOfMonth()
//        )
//
//
//        return transactions

        TODO("No reason")
    }
}