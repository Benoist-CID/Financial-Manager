package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionType

/**
 * Computes tha total amount of a List<FinancialInput>
 *
 * @return Sum of all amounts as a Float
 */
fun List<Transaction>.sum(): Float {
    var sum = 0.0F

    this.forEach { input ->
        when (input.type) {
            TransactionType.Income -> sum += input.amount
            TransactionType.Expense -> sum -= input.amount
        }
    }

    return sum
}
