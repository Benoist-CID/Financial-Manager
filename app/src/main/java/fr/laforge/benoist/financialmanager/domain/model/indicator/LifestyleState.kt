package fr.laforge.benoist.financialmanager.domain.model.indicator

/**
 * Snapshot of the user's recurring-expense lifestyle.
 *
 * Computed as `recurringExpenses / recurringIncome`. A [ratio] of 0.30 means
 * 30 % of recurring income goes to fixed costs; higher values indicate less
 * financial flexibility.
 *
 * @property ratio  The expense-to-income ratio (0..∞). Values > 1 mean expenses exceed income.
 * @property status The [LifestyleStatus] band determined by [ratio].
 */
data class LifestyleState(
    val ratio: Float = 0f,
    val status: LifestyleStatus = LifestyleStatus.Healthy
)

