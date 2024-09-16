package fr.laforge.benoist.financialmanager.views.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCase
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime

class AddTransactionViewModel : ViewModel(), KoinComponent {
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private var isPeriodic: Boolean = false
    var period: TransactionPeriod = TransactionPeriod.Monthly
    private var startDate: LocalDateTime = LocalDateTime.now()
    private var endDate: LocalDateTime = LocalDateTime.now()

    private val createTransactionUseCase: CreateTransactionUseCase by inject()

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

                val result = createTransactionUseCase.execute(transaction).first()

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
