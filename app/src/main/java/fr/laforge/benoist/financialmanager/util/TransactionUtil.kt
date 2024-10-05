package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.util.toLocalDateTime
import fr.laforge.benoist.util.toMilliseconds


fun Transaction.exportToCsvFormat(): String {
    return "$uid;${dateTime.toMilliseconds()};$amount;$description;$type;$isPeriodic;$period;$parent;$category"
}

fun transactionFromCsv(csv: String): Transaction {
    val split = csv.split(';')
    val uid = split[0].toInt()
    val dateTime = toLocalDateTime(split[1].toLong())
    val amount = split[2].toFloat()
    val description = split[3]
    val type = TransactionType.from(split[4])!!
    val isPeriodic = split[5].toBoolean()
    val period = TransactionPeriod.from(split[6])!!
    val parent = split[7].toInt()
    val category = TransactionCategory.from(split[8])!!

    return Transaction(
        uid,
        dateTime,
        amount,
        description,
        type,
        isPeriodic,
        period,
        parent,
        category
    )
}

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

/**
 * Checks if a Transaction is a child transaction or not
 *
 * @return Boolean
 */
fun Transaction.isChildTransaction(): Boolean {
    return this.parent != 0
}
