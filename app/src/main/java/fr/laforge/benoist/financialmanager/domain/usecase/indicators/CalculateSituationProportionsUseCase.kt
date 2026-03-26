package fr.laforge.benoist.financialmanager.domain.usecase.indicators

/**
 * Use case that normalises four budget categories into proportions for the Situation Card.
 *
 * The Situation Card visualises how a user's monthly budget is distributed across
 * income, recurring expenses, regular (one-off) expenses, and a savings target.
 * Each category is expressed as a fraction of the combined total so the four values
 * always sum to 1.0 (or all to 0.0 when the total is zero).
 */
class CalculateSituationProportionsUseCase {

    /**
     * Returns a list of four floats representing the normalised proportions
     * `[Income, RecurringExpense, RegularExpense, Savings]`.
     *
     * @param income            The user's total income for the period.
     * @param recurringExpenses The sum of fixed/recurring expenses (e.g. rent, subscriptions).
     * @param regularExpenses   The sum of variable one-off expenses.
     * @param savingsTarget     The amount the user intends to save.
     * @return A [List] of four [Float] values in the range `[0, 1]` whose sum ≈ 1.0,
     *   or all zeros if [income], [recurringExpenses], [regularExpenses] and
     *   [savingsTarget] are all zero.
     *
     * @note The `total` denominator is **the sum of all four inputs**, not just the income.
     * This is intentional: the chart shows the *relative weight* of each category within
     * the overall budget envelope, not each category as a fraction of income alone.
     * Example: income=50, recurringExpenses=25, regularExpenses=25, savingsTarget=0
     * → total=100 → proportions=[0.5, 0.25, 0.25, 0.0].
     * If you instead divided by income only, expenses > income would produce values > 1,
     * which the chart cannot render correctly.
     */
    operator fun invoke(
        income: Float,
        recurringExpenses: Float,
        regularExpenses: Float,
        savingsTarget: Float
    ): List<Float> {
        val total = income + recurringExpenses + regularExpenses + savingsTarget

        if (total == 0f) {
            return listOf(0f, 0f, 0f, 0f)
        }

        return listOf(
            income / total,
            recurringExpenses / total,
            regularExpenses / total,
            savingsTarget / total
        )
    }
}
