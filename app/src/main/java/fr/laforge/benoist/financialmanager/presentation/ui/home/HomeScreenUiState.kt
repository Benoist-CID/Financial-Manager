package fr.laforge.benoist.financialmanager.presentation.ui.home

data class HomeScreenUiState (
    val query: String = "",
    val displayPeriodicDeletionDialog: Boolean = false,
    val displayDeleteDialog: Boolean = false,
)
