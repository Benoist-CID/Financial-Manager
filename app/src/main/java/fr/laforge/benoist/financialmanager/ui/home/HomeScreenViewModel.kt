package fr.laforge.benoist.financialmanager.ui.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.controller.PreferencesController
import fr.laforge.benoist.financialmanager.domain.usecases.CheckIfTransactionIsPeriodicUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeletePeriodicStatus
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCaseStatus
import fr.laforge.benoist.financialmanager.util.exportToCsvFormat
import fr.laforge.benoist.financialmanager.util.getFirstDayOfMonth
import fr.laforge.benoist.financialmanager.util.getLastDayOfMonth
import fr.laforge.benoist.financialmanager.util.sum
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


class HomeScreenViewModel(
    private val repository: FinancialRepository,
    private val checkIfTransactionIsPeriodicUseCase: CheckIfTransactionIsPeriodicUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    preferencesController: PreferencesController,
) : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()
    val deletePeriodicStatusFlow: Flow<DeletePeriodicStatus> = MutableSharedFlow()

    val periodicAmount: Flow<Float> = repository.getAllPeriodicTransactionsByType(
        type = TransactionType.Expense
    ).map {
        it.sum()
    }

    var allTransactions: Flow<List<Transaction>> = repository.getAllInDateRange(
        startDate = LocalDateTime.now().getFirstDayOfMonth(),
        endDate = LocalDateTime.now().getLastDayOfMonth().plusDays(1)
    )

    val allCurrentMonthTransactionsAmount = allTransactions.map { transactions ->
        transactions.filter { transaction ->
            transaction.type == TransactionType.Expense
        }.sum()
    }

    val income: Flow<Float> =
        allTransactions.map { transactions ->
            transactions.filter { transaction ->
                transaction.type == TransactionType.Income
            }.sum()
        }

    val regularExpenses: Flow<Float> = allTransactions.map {
        it.filter { transaction -> transaction.type == TransactionType.Expense && transaction.parent == 0 && !transaction.isPeriodic }
            .sum()
    }

    val savingsTarget = preferencesController.getSavingTarget()

    suspend fun deleteTransaction(
        transaction: Transaction
    ) {
        if (checkIfTransactionIsPeriodicUseCase(transaction)) {
               
        }
    }

    fun saveDb(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getAll().first { transactions ->
                    val sb = StringBuilder()
                    transactions.forEach {
                        sb.append(it.exportToCsvFormat() + "\n")
                    }

                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    // type of the content to be shared
                    sharingIntent.type = "text/plain"
                    // Body of the content
                    val shareBody = sb.toString()
                    // subject of the content. you can share anything
                    val shareSubject = "DB snapshot ${LocalDateTime.now()}"
                    // passing body of the content
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

                    // passing subject of the content
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    context.startActivity(Intent.createChooser(sharingIntent, "Share using"))

                    true
                }
            }
        }
    }

    fun updateSearch(newVal: String) {
        _uiState.update { currentState ->
            currentState.copy(query = newVal)
        }
    }

    companion object {
        // This must be moved in settings when it will be implemented
        const val START_DAY = 24
    }
}
