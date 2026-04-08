package fr.laforge.benoist.financialmanager.presentation.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.usecase.sync.ApplySyncMatchUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.sync.CreateTransactionFromBankUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.sync.ParseCsvBankTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.sync.RunSyncUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * ViewModel for the Sync Review screen.
 *
 * Orchestrates the CSV-based bank-sync flow:
 * 1. [importCsv] parses the raw CSV content and runs the sync matcher for the current month,
 *    then populates [uiState] with match candidates.
 * 2. [confirmMatch] applies a candidate (PENDING → IN_SYNC) and removes it from the list.
 * 3. [skipMatch] removes a candidate without persisting any change (user disagrees).
 * 4. [createFromBank] imports an unmatched bank transaction as a new NEW_FROM_BANK entry.
 * 5. [resetToIdle] returns the screen to its initial state so the user can pick another file.
 *
 * @property runSyncUseCase                   Matches filtered bank transactions against app transactions.
 * @property applySyncMatchUseCase            Persists a confirmed match (updates syncStatus).
 * @property createTransactionFromBankUseCase Creates a new transaction from a bank entry.
 * @property parseCsvBankTransactionsUseCase  Parses raw CSV text into [BankTransaction]s.
 * @property dispatcher Coroutine dispatcher for IO work; inject [Dispatchers.Unconfined]
 *   in tests for synchronous execution.
 */
class SyncReviewViewModel(
    private val runSyncUseCase: RunSyncUseCase,
    private val applySyncMatchUseCase: ApplySyncMatchUseCase,
    private val createTransactionFromBankUseCase: CreateTransactionFromBankUseCase,
    private val parseCsvBankTransactionsUseCase: ParseCsvBankTransactionsUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncReviewUiState())

    /** Current UI state. Collect this in the composable. */
    val uiState: StateFlow<SyncReviewUiState> = _uiState.asStateFlow()

    /**
     * Parses [csvContent] and runs the sync matcher for the current calendar month.
     *
     * The CSV may span any date range (e.g. the full year); only transactions falling within
     * the first day of the current month through today are matched against app data.
     *
     * Transitions through Loading → (Matches | Error) states.
     *
     * @param csvContent Decoded CSV text read from the bank export file.
     */
    fun importCsv(csvContent: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isIdle = false, isLoading = true, error = null) }

            val parseResult = withContext(dispatcher) {
                parseCsvBankTransactionsUseCase(csvContent)
            }

            val bankTransactions = parseResult.getOrElse { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "CSV parse error")
                }
                return@launch
            }

            val syncResult = withContext(dispatcher) {
                runSyncUseCase(
                    bankTransactions = bankTransactions,
                    dateFrom = LocalDate.now().withDayOfMonth(1),
                    dateTo = LocalDate.now(),
                )
            }
            syncResult
                .onSuccess { matches ->
                    _uiState.update { it.copy(isLoading = false, matches = matches) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "Unknown error")
                    }
                }
        }
    }

    /**
     * Resets the screen to its initial idle state so the user can pick a different CSV file.
     */
    fun resetToIdle() {
        _uiState.update { SyncReviewUiState() }
    }

    /**
     * Confirms [matchResult]: marks the app transaction as IN_SYNC and removes the candidate
     * from the review list.
     *
     * @param matchResult The candidate the user confirmed as correct.
     */
    fun confirmMatch(matchResult: TransactionMatchResult) {
        viewModelScope.launch {
            withContext(dispatcher) { applySyncMatchUseCase(matchResult) }
            _uiState.update { it.copy(matches = it.matches - matchResult) }
        }
    }

    /**
     * Skips [matchResult] without making any persistent change.
     * The candidate is removed from the review list but the transaction stays PENDING.
     *
     * @param matchResult The candidate the user decided to ignore.
     */
    fun skipMatch(matchResult: TransactionMatchResult) {
        _uiState.update { it.copy(matches = it.matches - matchResult) }
    }

    /**
     * Imports [bankTransaction] as a new NEW_FROM_BANK transaction and removes all match
     * candidates for that bank entry from the review list.
     *
     * Use this when the user dismisses all app candidates for a bank entry but still wants
     * the transaction recorded in the app.
     *
     * @param bankTransaction The bank statement entry to import.
     */
    fun createFromBank(bankTransaction: BankTransaction) {
        viewModelScope.launch {
            withContext(dispatcher) { createTransactionFromBankUseCase(bankTransaction) }
            _uiState.update { state ->
                state.copy(matches = state.matches.filter { it.bankTransaction != bankTransaction })
            }
        }
    }
}
