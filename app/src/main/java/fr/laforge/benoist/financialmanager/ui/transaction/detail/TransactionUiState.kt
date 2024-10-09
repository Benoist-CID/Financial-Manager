package fr.laforge.benoist.financialmanager.ui.transaction.detail

import fr.laforge.benoist.model.Transaction

data class TransactionUiState(
    val transaction: Transaction = Transaction()
)
