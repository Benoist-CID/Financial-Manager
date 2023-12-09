package fr.laforge.benoist.financialmanager.views.transaction.add

import fr.laforge.benoist.model.TransactionType

data class AddTransactionUiState(
    val transactionType: TransactionType = TransactionType.Expense,
    val amount: String = "0",
    val description: String = ""
)
