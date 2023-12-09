package fr.laforge.benoist.model

import java.time.LocalDateTime

data class Transaction(
    var uid: Int = 0,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var amount: Float = 0F,
    var description: String = "",
    var type: TransactionType = TransactionType.Expense,
    var isPeriodic: Boolean = false,
    var period: TransactionPeriod = TransactionPeriod.None
) {
    fun exportToCsvFormat(): String {
        return "$uid;$dateTime;$amount;$description;$type;$isPeriodic;$period"
    }
}

fun transactionFromCsv(csv: String): Transaction {
    val split = csv.split(';')
    val uid = split[0].toInt()
    val dateTime = LocalDateTime.parse(split[1])
    val amount = split[2].toFloat()
    val description = split[3]
    val type = TransactionType.from(split[4])!!
    val isPeriodic = split[5].toBoolean()
    val period = TransactionPeriod.from(split[6])!!

    return Transaction(
        uid,
        dateTime,
        amount,
        description,
        type,
        isPeriodic,
        period
    )
}
