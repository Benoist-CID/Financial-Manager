package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.Transaction
import kotlinx.coroutines.flow.Flow

interface CreateTransactionUseCase {
    fun execute(
        transaction: Transaction
    ): Flow<Boolean>
}
