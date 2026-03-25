package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository

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

    override suspend fun invoke(transaction: Transaction): Boolean {
        // Creates transaction
        val id = repository.createTransaction(
            transaction
        )

        // If transaction is periodic, creates the associated regular transaction for the current
        // period
        if (transaction.isPeriodic) {
            val regularTransaction = transaction.copy(uid = 0, isPeriodic = false, parent = id.toInt())
            repository.createTransaction(
                regularTransaction
            )
        }

        return true
    }
}
