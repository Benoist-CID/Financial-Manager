package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateTransactionUseCaseImpl: CreateTransactionUseCase, KoinComponent {
    private val repository: FinancialRepository by inject()

    override fun execute(
        transaction: Transaction
    ) = flow {
        // Creates transaction
        val id = repository.createTransaction(
            transaction
        )

        // If transaction is periodic, creates the associated regular transaction for the current
        // period
        if (transaction.isPeriodic) {
            val regularTransaction = transaction.copy(isPeriodic = false, parent = id.toInt())
            repository.createTransaction(
                regularTransaction
            )
        }

        emit(true)
    }
}
