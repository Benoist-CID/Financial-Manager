package fr.laforge.benoist.financialmanager.views.transaction.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import timber.log.Timber

class TransactionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val financialRepository: FinancialRepository
) : ViewModel(), KoinComponent {
    // TODO inject this through constructor

    val transactionId = savedStateHandle.get<Int>("transactionId") ?: 0

    val uiState: StateFlow<TransactionUiState> = financialRepository.get(uid = transactionId)
        .filterNotNull()
        .map {
            Timber.d("Map")
            TransactionUiState(transaction = it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionUiState()
        )
}
