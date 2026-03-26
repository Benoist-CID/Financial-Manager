package fr.laforge.benoist.financialmanager.presentation.ui.db

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ImportTransactionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * ViewModel for the database-import screen.
 *
 * Holds the raw CSV text entered by the user and delegates all parsing and
 * persistence work to [ImportTransactionsUseCase], keeping this class free of
 * domain or data-layer logic.
 *
 * @property importTransactionsUseCase Domain use case that parses CSV and persists transactions.
 *
 * @note [FinancialRepository] is intentionally absent — data access is fully encapsulated
 * in the use case layer to honour the Clean Architecture dependency rule.
 */
class ImportDbViewModel(
    private val importTransactionsUseCase: ImportTransactionsUseCase,
) : ViewModel() {

    var toBeImported by mutableStateOf("")
        private set

    /**
     * Updates the raw CSV text that will be imported.
     *
     * @param newValue The new CSV string from the UI input field.
     */
    fun updateToBeImported(newValue: String) {
        toBeImported = newValue
        Timber.d(newValue)
    }

    /**
     * Triggers the import of all transactions encoded in [toBeImported].
     *
     * Delegates entirely to [ImportTransactionsUseCase]; malformed lines are
     * silently skipped by the use case.
     */
    fun importDb() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                importTransactionsUseCase(toBeImported)
            }
        }
    }
}
