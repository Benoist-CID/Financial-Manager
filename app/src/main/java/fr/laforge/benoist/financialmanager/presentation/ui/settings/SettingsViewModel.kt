package fr.laforge.benoist.financialmanager.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ExportTransactionsListUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetAllRecurringTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetAllTransactionsUseCase
import fr.laforge.benoist.financialmanager.presentation.util.ExportService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getAllRecurringTransactionsUseCase: GetAllRecurringTransactionsUseCase,
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
     * Exports all transactions to a CSV file and triggers the export service.
     */
    fun saveDb() {
        viewModelScope.launch {
            val transactions = getAllTransactionsUseCase().first()
            exportTransactionsListUseCase(transactions).onSuccess { csvContent ->
                val subject = "DB snapshot ${LocalDateTime.now()}"
                exportService.export(csvContent, subject)
            }
        }
    }

    /**
     * Exports only the recurring transaction templates (both Expense and Income) to a CSV
     * file and triggers the export service.
     *
     * This is useful for backing up fixed commitments (subscriptions, salaries, loans)
     * without including the full transaction history.
     */
    fun saveRecurringDb() {
        viewModelScope.launch {
            val transactions = getAllRecurringTransactionsUseCase().first()
            exportTransactionsListUseCase(transactions).onSuccess { csvContent ->
                val subject = "Recurring transactions snapshot ${LocalDateTime.now()}"
                exportService.export(csvContent, subject)
            }
        }
    }
}
