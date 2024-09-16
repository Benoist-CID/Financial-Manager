package fr.laforge.benoist.financialmanager.views.transaction.detail

import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionType

data class TransactionUiState(
    val transactionType: TransactionType = TransactionType.Expense,
    val transactionCategory: TransactionCategory = TransactionCategory.None,
    val amount: String = "0",
    val description: String = ""
)
