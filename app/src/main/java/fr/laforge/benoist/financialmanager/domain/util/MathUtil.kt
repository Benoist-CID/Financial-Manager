package fr.laforge.benoist.financialmanager.domain.util

fun getProportions(
    income: Float,
    recurringExpenses: Float,
    expenses: Float,
    savings: Float
): List<Float> {
    return listOf(
        recurringExpenses / income,
        expenses / income,
        (income - (recurringExpenses + expenses + savings))/income,
        savings / income,
    )
}
