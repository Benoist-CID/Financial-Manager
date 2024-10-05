package fr.laforge.benoist.financialmanager.domain.usecases

import fr.laforge.benoist.financialmanager.util.isChildTransaction
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface DeleteTransactionUseCase {
    suspend operator fun invoke(
        transaction: Transaction,
        onPeriodicChildTransaction: () -> DeletePeriodicStatus,
        onShouldDisplayConfirmationDialog: () -> Boolean,
    ): Result<Boolean>
}

enum class DeletePeriodicStatus {
    ThisOccurrenceOnly,
    AllOccurrences,
}

class DeleteTransactionUseCaseImpl(private val financialRepository: FinancialRepository) : DeleteTransactionUseCase {
    override suspend fun invoke(
        transaction: Transaction,
        onPeriodicChildTransaction: () -> DeletePeriodicStatus,
        onShouldDisplayConfirmationDialog: () -> Boolean,
    ): Result<Boolean> {
        return try {
            val deleteTransactionStatus = if (transaction.isChildTransaction()) {
                onPeriodicChildTransaction()
            } else {
                DeletePeriodicStatus.ThisOccurrenceOnly
            }

            val shouldDelete = onShouldDisplayConfirmationDialog()

            if (shouldDelete) {
                financialRepository.deleteTransaction(transaction)

                if (deleteTransactionStatus == DeletePeriodicStatus.AllOccurrences) {
                    deleteParentTransaction(transaction)
                }
                Result.success(true)
            } else {
                Result.success(false) // User canceled
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun deleteParentTransaction(transaction: Transaction) {
        financialRepository.get(uid = transaction.parent).map { parent ->
            financialRepository.deleteTransaction(parent)
        }.first()
    }
}
