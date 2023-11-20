package fr.laforge.benoist.financialmanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.util.sum
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeScreenViewModel : ViewModel(), KoinComponent {

    private val repository: FinancialRepository by inject()

    val availableAmount : StateFlow<Float> = repository.getAll().map {
        it.sum()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0F
    )

    val allTransactions : StateFlow<List<Transaction>> = repository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )
}
