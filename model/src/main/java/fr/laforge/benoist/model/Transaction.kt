package fr.laforge.benoist.model

import java.time.LocalDateTime

data class Transaction(
    var uid: Int = 0,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var amount: Float = 0F,
    var description: String = "",
    var type: InputType = InputType.Expense
)
