package fr.laforge.benoist.financialmanager.presentation.ui.home.situation.card

/**
 * The UI State for the Situation Card
 */
data class SituationCardState(
    val income: Float = 0f,
    val regularExpenses: Float = 0f,
    val recurringExpenses: Float = 0f,
    val savingsTarget: Float = 0f,
    val remainingBalance: Float = 0f,
    val dailyBudget: Float = 0f,
    val proportions: List<Float> = emptyList(),
)
