package fr.laforge.benoist.financialmanager.ui.transaction.add

import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionType

data class AddTransactionUiState(
    val transactionType: TransactionType = TransactionType.Expense,
    val transactionCategory: TransactionCategory = TransactionCategory.None,
    val amount: String = "0.0",
    val description: String = ""
)
