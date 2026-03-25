package fr.laforge.benoist.financialmanager.presentation.ui.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime

class AddTransactionViewModel(private val createTransactionUseCase: CreateTransactionUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private var isPeriodic: Boolean = false
    var period: TransactionPeriod = TransactionPeriod.Monthly
    private var startDate: LocalDateTime = LocalDateTime.now()
    private var endDate: LocalDateTime = LocalDateTime.now()

    fun updateAmount(amount: String) {
        _uiState.update { currentState ->
            currentState.copy(amount = amount)
        }
    }

    fun updateDescription(description: String) {
        _uiState.update { currentState ->
            currentState.copy(description = description)
        }
    }

    fun updateInputType(transactionType: TransactionType) {
        _uiState.update { currentState ->
            currentState.copy(transactionType = transactionType)
        }
    }

    fun updateTransactionCategory(transactionCategory: TransactionCategory) {
        _uiState.update { currentState ->
            currentState.copy(transactionCategory = transactionCategory)
        }
    }

    fun updateIsPeriodic(newState: Boolean) {
        Timber.d("updateIsPeriodic: $newState")
        isPeriodic = newState
    }

    fun updatePeriod(newPeriod: TransactionPeriod) {
        Timber.d("updatePeriod: $newPeriod")
        period = newPeriod
    }

    fun updateStartDate(newDate: LocalDateTime) {
        Timber.d("updateStartDate: $newDate")
        startDate = newDate
    }

    fun updateEndDate(newDate: LocalDateTime) {
        Timber.d("updateEndDate: $newDate")
        endDate = newDate
    }

    fun createTransaction() {
        viewModelScope.launch {
            withContext(Dispatchers.IO + Job()) {
                val date = if (isPeriodic) startDate else LocalDateTime.now()

                val transaction =
                    Transaction(
                        dateTime = date,
                        description = _uiState.value.description,
                        amount = _uiState.value.amount.toFloat(),
                        type = _uiState.value.transactionType,
                        isPeriodic = isPeriodic,
                        period = period,
                        category = _uiState.value.transactionCategory
                    )

                val result = createTransactionUseCase(transaction)

                if (result) {
                    // TODO Display successfully created transaction message
                    Timber.i("Successfully create transaction")
                } else {
                    // TODO Display error message here
                    Timber.e("Error creating transaction")
                }
            }
        }
    }
}
