package fr.laforge.benoist.financialmanager.views.db

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.credenceid.util.FileUtil
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.transactionFromCsv
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ImportDbViewModel : ViewModel(), KoinComponent {
    private val repository: FinancialRepository by inject()

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
