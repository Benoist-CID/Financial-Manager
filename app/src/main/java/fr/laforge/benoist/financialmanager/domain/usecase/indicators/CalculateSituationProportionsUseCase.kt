package fr.laforge.benoist.financialmanager.domain.usecase.indicators

/**
 * A use case to calculate the proportions for the Situation Card.
 */
class CalculateSituationProportionsUseCase {

    /**
     * returns a list of 4 floats representing the normalized proportions
     * [Income, RecurringExpense, RegularExpense, Savings]
     * Sum is always roughly 1.0 (unless total is 0).
     *
     * @param income The total income.
     * @param recurringExpenses The total recurring expenses.
     * @param regularExpenses The total regular expenses.
     * @param savingsTarget The total savings target.
     *
     * @return A list of 4 floats representing the normalized proportions.
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
