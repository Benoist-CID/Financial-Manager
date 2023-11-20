package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType

/**
 * Computes tha total amount of a List<FinancialInput>
 *
 * @return Sum of all amounts as a Float
 */
fun List<Transaction>.sum(): Float {
    var sum = 0.0F

    this.forEach { input ->
        when (input.type) {
            InputType.Income -> sum += input.amount
            InputType.Expense -> sum -= input.amount
        }
    }

    return sum
}
