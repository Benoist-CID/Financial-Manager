package fr.laforge.benoist.financialmanager.views.transaction.detail

import androidx.lifecycle.ViewModel
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionDetailsViewModel(transactionId: Int) : ViewModel(), KoinComponent {
    private val repository: FinancialRepository by inject()

    val transaction: Flow<Transaction> = repository.get(uid = transactionId)
}
