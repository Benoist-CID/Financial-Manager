package fr.laforge.benoist.financialmanager.ui.addinput

import fr.laforge.benoist.model.InputType

data class AddInputUiState(
    var description: String = "",
    var amount: Float = 0.0F,
    var inputType: InputType = InputType.Expense
)