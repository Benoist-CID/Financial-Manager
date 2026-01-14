package fr.laforge.benoist.financialmanager.presentation.ui.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractor
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRegularExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.util.exportToCsvFormat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


class HomeScreenViewModel(
    private val repository: FinancialRepository,
    private val transactionInteractor: TransactionInteractor,
    getMonthStartingBalanceUseCase: GetMonthStartingBalanceUseCase,
    preferencesRepository: PreferencesRepository,
    getNonRecurringIncomeUseCase: GetNonRecurringIncomeUseCase,
    getRecurringIncomeUseCase: GetRecurringIncomeUseCase,
    getRecurringExpensesUseCase: GetRecurringExpensesUseCase,
    getNonRecurringExpensesUseCase: GetRegularExpensesUseCase,
) : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    val periodicAmount: Flow<Float> = getRecurringExpensesUseCase()

    var allTransactions: Flow<List<Transaction>> = repository.getTransactions(
        filter = TransactionFilter(
            type = null,
            startDate = null,
            endDate = null,
            descriptionQuery = null,
            isPeriodic = false,
            parentId = null
        )
    ).map { transactions ->
        transactions.sortedByDescending { it.dateTime }
    }

    val allCurrentMonthTransactionsAmount = combine(getNonRecurringExpensesUseCase(), getRecurringExpensesUseCase()) { nonRecurring, recurring ->
        nonRecurring + recurring
    }

    val income: Flow<Float> = combine(getNonRecurringIncomeUseCase(), getRecurringIncomeUseCase()) { nonRecurring, recurring ->
        nonRecurring + recurring
    }

    val regularExpenses: Flow<Float> = getNonRecurringExpensesUseCase()

    val savingsTarget = preferencesRepository.getSavingTarget()

    val startBalanceFlow = getMonthStartingBalanceUseCase(LocalDateTime.now())

    fun isPeriodicTransaction(transaction: Transaction) =
        transactionInteractor.isPeriodicTransaction(transaction)

    fun deleteTransaction(
        transaction: Transaction,
        shouldDeleteParent: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        val deleteTransactionType = if (shouldDeleteParent) {
            DeleteTransactionType.AllOccurrences
        } else {
            DeleteTransactionType.ThisOccurrenceOnly
        }

        viewModelScope.launch(dispatcher) {
            transactionInteractor.deleteTransaction(
                transaction = transaction,
                deleteTransactionType = deleteTransactionType
            )
        }
    }

    fun updateSearch(newVal: String) {
        _uiState.update { currentState ->
            currentState.copy(query = newVal)
        }
    }

    val uiListFlow = combine(startBalanceFlow, allTransactions) { startBalance, transactions ->
        val headerItem = Transaction(
            uid = -1, // Fake ID
            description = "Solde précédent", // "Previous Balance"
            amount = startBalance,
            dateTime = LocalDateTime.now().withDayOfMonth(1), // First of the month
            type = if (startBalance >= 0) TransactionType.Income else TransactionType.Expense,
            // Add a special flag or category so you can style it differently (e.g. grayed out)
            category = TransactionCategory.None
        )
        // Return a list where the Balance is the very first item
        transactions + listOf(headerItem)
    }

    companion object {
        // This must be moved in settings when it will be implemented
        const val START_DAY = 29
    }
}
