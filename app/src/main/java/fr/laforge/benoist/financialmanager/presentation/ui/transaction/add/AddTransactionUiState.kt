package fr.laforge.benoist.financialmanager.presentation.ui.transaction.add

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType

data class AddTransactionUiState(
    val transactionType: TransactionType = TransactionType.Expense,
    val transactionCategory: TransactionCategory = TransactionCategory.None,
    val amount: String = "0.0",
    val description: String = ""
)
