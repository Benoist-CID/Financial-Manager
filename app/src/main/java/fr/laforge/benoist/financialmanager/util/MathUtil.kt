package fr.laforge.benoist.financialmanager.util

fun getProportions(income: Float, amount: Float, savings: Float): List<Float> {
    return listOf((income - (amount + savings))/income, amount / income, savings / income)
}
