package fr.laforge.benoist.financialmanager.domain.usecases

import fr.laforge.benoist.financialmanager.util.isChildTransaction
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface DeleteTransactionUseCase {
    suspend operator fun invoke(
        transaction: Transaction,
        onPeriodicChildTransaction: () -> DeletePeriodicStatus,
        onShouldDisplayConfirmationDialog: () -> Boolean,
    ): Flow<DeleteTransactionUseCaseStatus>
}

enum class DeletePeriodicStatus {
    ThisOccurrenceOnly,
    AllOccurrences,
}

enum class DeleteTransactionUseCaseStatus {
    ShouldDisplayPeriodicDialog,
    ShouldDisplayConfirmationDialog,
    Success,
    Error,
}

class DeleteTransactionUseCaseImpl(private val financialRepository: FinancialRepository) :
    DeleteTransactionUseCase {
    override suspend fun invoke(
        transaction: Transaction,
        onPeriodicChildTransaction: () -> DeletePeriodicStatus,
        onShouldDisplayConfirmationDialog: () -> Boolean,
    ) = flow {
        if (transaction.isChildTransaction()) {
            emit(DeleteTransactionUseCaseStatus.ShouldDisplayPeriodicDialog))
        }

        val deletePeriodicStatus = onPeriodicChildTransaction()

        val shouldDelete = onShouldDisplayConfirmationDialog()

        if (shouldDelete) {
            financialRepository.deleteTransaction(transaction)

            if (deletePeriodicStatus == DeletePeriodicStatus.AllOccurrences) {
                deleteParentTransaction(transaction)
            }
            Result.success(true)
        } else {
            Result.success(false) // User canceled
        }
    }

    private suspend fun deleteParentTransaction(transaction: Transaction) {
        financialRepository.get(uid = transaction.parent).map { parent ->
            financialRepository.deleteTransaction(parent)
        }.first()
    }
}
