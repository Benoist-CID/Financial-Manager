package fr.laforge.benoist.util

fun getProportions(
    income: Float,
    recurringExpenses: Float,
    expenses: Float,
    savings: Float
): List<Float> {
    return listOf(
        (income - (recurringExpenses + expenses + savings))/income,
        recurringExpenses / income,
        expenses / income,
        savings / income
    )
}
