package fr.laforge.benoist.financialmanager.views.transaction.detail

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionDetailsViewModel(transactionId: Int) : ViewModel(), KoinComponent {
    // TODO inject this through constructor
    private val repository: FinancialRepository by inject()
    val uiState: StateFlow<TransactionUiState> = repository.get(uid = transactionId)
        .filterNotNull()
        .map {
            TransactionUiState(transaction = it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionUiState()
        )
}
