package fr.laforge.benoist.financialmanager.views.addinput

import fr.laforge.benoist.model.InputType

data class AddTransactionUiState(
    var description: String = "",
    var amount: Float = 0.0F,
    var inputType: InputType = InputType.Expense
)