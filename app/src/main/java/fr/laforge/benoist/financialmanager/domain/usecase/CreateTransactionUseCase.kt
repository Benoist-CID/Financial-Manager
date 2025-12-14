package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface CreateTransactionUseCase {
    fun execute(
        transaction: Transaction
    ): Flow<Boolean>

    operator fun invoke(transaction: Transaction): Boolean
}
