package fr.laforge.benoist.financialmanager.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.util.sum
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeScreenViewModel : ViewModel(), KoinComponent {
    private val repository: FinancialRepository by inject()

    val availableAmount : Flow<Float> = repository.getAll().map {
        it.sum()
    }

    val allTransactions : Flow<List<Transaction>> = repository.getAll()

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            withContext(Dispatchers.IO + Job()) {
                repository.deleteTransaction(transaction = transaction)
            }
        }
    }
}
