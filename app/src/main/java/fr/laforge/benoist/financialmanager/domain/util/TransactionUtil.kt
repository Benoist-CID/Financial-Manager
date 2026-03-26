package fr.laforge.benoist.financialmanager.domain.util

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.util.toLocalDateTime
import fr.laforge.benoist.financialmanager.domain.util.toMilliseconds


/**
 * Serialises this [Transaction] into a semicolon-delimited CSV row.
 *
 * Column order: uid ; dateTime(ms) ; amount ; description ; type ; isPeriodic ; period ; parent ; category
 *
 * @return A single-line CSV string representation of this transaction.
 */
fun Transaction.exportToCsvFormat(): String {
    return "$uid;${dateTime.toMilliseconds()};$amount;$description;$type;$isPeriodic;$period;$parent;$category"
}

/**
 * Parses a semicolon-delimited CSV row produced by [exportToCsvFormat] into a [Transaction].
 *
 * @param csv A single CSV line as written by [exportToCsvFormat].
 * @return The reconstructed [Transaction].
 * @throws NumberFormatException if numeric fields cannot be parsed.
 * @throws NullPointerException if [TransactionType], [TransactionPeriod], or
 *   [TransactionCategory] lookup returns `null` for an unknown value.
 */
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
