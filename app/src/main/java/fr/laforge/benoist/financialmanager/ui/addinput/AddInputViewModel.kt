package fr.laforge.benoist.financialmanager.ui.addinput

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

class AddInputViewModel : ViewModel(), KoinComponent {
    var amount: Float by mutableFloatStateOf(1.0F)
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
            this.amount = amount.toFloat()
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
                repository.createFinancialInput(
                    transaction = Transaction(
                        description = description,
                        amount = amount,
                        type = inputType
                    )
                )
            }
        }
    }
}
