package fr.laforge.benoist.financialmanager.presentation.ui.transaction.add

import fr.laforge.benoist.financialmanager.domain.model.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.TransactionType

data class AddTransactionUiState(
    val transactionType: TransactionType = TransactionType.Expense,
    val transactionCategory: TransactionCategory = TransactionCategory.None,
    val amount: String = "0.0",
    val description: String = ""
)
