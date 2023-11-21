package fr.laforge.benoist.financialmanager.views.addinput

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class AddTransactionViewModel : ViewModel(), KoinComponent {
    var amount: String by mutableStateOf("")
    var description: String by mutableStateOf("")
    var inputType: InputType by mutableStateOf(InputType.Expense)

    private val repository : FinancialRepository by inject()

    fun getAllInputs() {
        viewModelScope.launch {
            withContext(Dispatchers.IO + Job()) {
                var inputs = repository.getAllExpenses()
                inputs.first().forEach {
                    Timber.i("$it")
                }

                inputs = repository.getAllIncomes()
                inputs.first().forEach {
                    Timber.i("$it")
                }
            }
        }
    }

    fun updateAmount(amount: String) {
        try {
            this.amount = amount
        } catch (e: Exception) {
            // Nothing
        }
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun updateInputType(type: Boolean) {
        inputType = when(type) {
            true -> InputType.Expense
            false -> InputType.Income
        }
    }

    fun createFinancialInput() {
        viewModelScope.launch {
            withContext(Dispatchers.IO + Job()) {
                repository.createTransaction(
                    transaction = Transaction(
                        description = description,
                        amount = amount.toFloat(),
                        type = inputType
                    )
                )
            }
        }
    }
}
