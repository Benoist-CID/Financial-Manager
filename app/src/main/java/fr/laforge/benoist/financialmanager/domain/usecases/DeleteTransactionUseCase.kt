package fr.laforge.benoist.financialmanager.domain.usecases

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface DeleteTransactionUseCase {
    suspend operator fun invoke(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType = DeleteTransactionType.ThisOccurrenceOnly,
    ): Result<Boolean>
}

enum class DeleteTransactionType {
    ThisOccurrenceOnly,
    AllOccurrences,
}

class DeleteTransactionUseCaseImpl(private val financialRepository: FinancialRepository) :
    DeleteTransactionUseCase {
    override suspend fun invoke(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType,
    ): Result<Boolean> {
        financialRepository.deleteTransaction(transaction)

        if (deleteTransactionType == DeleteTransactionType.AllOccurrences) {
            deleteParentTransaction(transaction)
        }

        return Result.success(true)
    }

    private suspend fun deleteParentTransaction(transaction: Transaction) {
        financialRepository.get(uid = transaction.parent).map { parent ->
            financialRepository.deleteTransaction(parent)
        }.first()
    }
}
