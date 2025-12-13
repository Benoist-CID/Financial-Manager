package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.model.Transaction

interface CheckIfTransactionIsPeriodicUseCase {
    operator fun invoke(transaction: Transaction): Boolean
}

class CheckIfTransactionIsPeriodicUseCaseImpl : CheckIfTransactionIsPeriodicUseCase {
    override fun invoke(transaction: Transaction): Boolean {
        return transaction.parent != 0
    }
}
