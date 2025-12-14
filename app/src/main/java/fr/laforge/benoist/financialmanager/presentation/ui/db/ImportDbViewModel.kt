package fr.laforge.benoist.financialmanager.presentation.ui.db

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.util.transactionFromCsv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ImportDbViewModel(private val repository: FinancialRepository) : ViewModel() {
    var toBeImported by mutableStateOf("")
        private set

    fun updateToBeImported(newValue: String) {
        toBeImported = newValue
        Timber.d(newValue)
    }

    fun importDb() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val transactionsList = mutableListOf<Transaction>()
                toBeImported.split('\n').forEach {
                    try{
                        transactionsList.add(transactionFromCsv(it))
                    } catch (e: Exception) {
                        // Do Nothing
                    }
                }

                transactionsList.forEach {
                    repository.createTransaction(it)
                }
            }
        }
    }
}
