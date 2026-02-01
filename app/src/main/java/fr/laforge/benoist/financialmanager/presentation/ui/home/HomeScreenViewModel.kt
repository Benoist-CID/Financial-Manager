package fr.laforge.benoist.financialmanager.presentation.ui.home

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractor
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth


class HomeScreenViewModel(
    private val transactionInteractor: TransactionInteractor,
    getMonthStartingBalanceUseCase: GetMonthStartingBalanceUseCase,
    getNonRecurringIncomeUseCase: GetNonRecurringIncomeUseCase,
    getRecurringIncomeUseCase: GetRecurringIncomeUseCase,
    getMonthlyTransactionsUseCase: GetMonthlyTransactionsUseCase,
) : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()
    private val _currentMonth = MutableStateFlow(YearMonth.now())

    private val _searchQuery = MutableStateFlow("")

    // 2. The Reactive List
    // Whenever Month OR Search changes, the Use Case is re-executed automatically.
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTransactions: StateFlow<List<Transaction>> = combine(
        _currentMonth,
        _searchQuery
    ) { month, query ->
        Pair(month, query)
    }.flatMapLatest { (month, query) ->
        getMonthlyTransactionsUseCase(
            month = month,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val income: Flow<Float> = combine(
        getNonRecurringIncomeUseCase(),
        getRecurringIncomeUseCase()
    ) { nonRecurring, recurring ->
        nonRecurring + recurring
    }


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
}
