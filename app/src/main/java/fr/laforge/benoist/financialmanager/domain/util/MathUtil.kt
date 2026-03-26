package fr.laforge.benoist.financialmanager.domain.util

/**
 * Decomposes a budget into four proportions relative to income.
 *
 * Each element expresses its category as a fraction of [income]:
 * - index 0: remaining balance ratio = `(income - recurringExpenses - expenses - savings) / income`
 * - index 1: recurring expenses ratio = `recurringExpenses / income`
 * - index 2: variable expenses ratio  = `expenses / income`
 * - index 3: savings ratio            = `savings / income`
 *
 * @param income            Total income for the period. Must be > 0; if zero all values are NaN.
 * @param recurringExpenses Total fixed (recurring) costs.
 * @param expenses          Total variable (one-off) costs.
 * @param savings           Planned savings amount.
 * @return A [List] of four [Float] values. Values may exceed 1 or be negative when costs
 *   exceed income.
 *
 * @note The denominator is [income] (not the total of all four values). This means values
 *   show each category as a share of earned income, which can exceed 1 when spending
 *   outstrips income. Contrast with [CalculateSituationProportionsUseCase] which uses the
 *   sum of all inputs as the denominator.
 */
fun getProportions(
    income: Float,
    recurringExpenses: Float,
    expenses: Float,
    savings: Float
): List<Float> {
    return listOf(
        (income - (recurringExpenses + expenses + savings)) / income,
        recurringExpenses / income,
        expenses / income,
        savings / income,
    )
}
