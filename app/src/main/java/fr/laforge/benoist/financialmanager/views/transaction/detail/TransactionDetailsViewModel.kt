package fr.laforge.benoist.financialmanager.views.transaction.detail

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.views.transaction.add.AddTransactionUiState
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionDetailsViewModel(private val transactionId: Int) : ViewModel(), KoinComponent,
    DefaultLifecycleObserver {
    // TODO inject this through constructor
    private val repository: FinancialRepository by inject()

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun getTransaction() {
        viewModelScope.launch {
            repository.get(uid = transactionId).collectLatest { tr ->
                _uiState.update {
                     it.copy(description = tr.description, amount = tr.amount.toString(), transactionType = tr.type, transactionCategory = tr.category)
                }
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        getTransaction()
    }
}
