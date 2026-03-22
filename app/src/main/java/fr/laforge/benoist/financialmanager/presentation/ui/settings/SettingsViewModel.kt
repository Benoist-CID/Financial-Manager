package fr.laforge.benoist.financialmanager.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ExportTransactionsListUseCase
import fr.laforge.benoist.financialmanager.presentation.util.ExportService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val repository: FinancialRepository,
    private val exportTransactionsListUseCase: ExportTransactionsListUseCase,
    private val exportService: ExportService,
) : ViewModel() {
    val savingsTarget = preferencesRepository.getSavingTarget()

    fun setSavingsTarget(newVal: Float) {
        viewModelScope.launch {
            preferencesRepository.setSavingsTarget(value = newVal)
        }
    }

    /**
     * Saves the current database to a CSV format and triggers the export service.
     */
    fun saveDb() {
        viewModelScope.launch {
            val transactions = repository.getTransactions(TransactionFilter.all()).first()
            exportTransactionsListUseCase(transactions).onSuccess { csvContent ->
                val subject = "DB snapshot ${LocalDateTime.now()}"
                exportService.export(csvContent, subject)
            }
        }
    }
}
