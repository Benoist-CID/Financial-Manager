package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.flow

/**
 * Default implementation of [CreateTransactionUseCase].
 *
 * Persists a new transaction via the repository. When the transaction is periodic,
 * a non-periodic child instance for the current period is automatically created
 * alongside the template.
 *
 * @property repository The [FinancialRepository] used to persist transactions.
 */
class CreateTransactionUseCaseImpl(
    private val repository: FinancialRepository
) : CreateTransactionUseCase {

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

    override fun invoke(transaction: Transaction): Boolean {
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

        return true
    }
}
