package fr.laforge.benoist.repository.implementations.room.entity

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDateTime

class TransactionEntityTest {
    @Test
    fun `Tests fromModel method`() {
        val transaction = Transaction(
            uid = 1,
            dateTime = LocalDateTime.parse("2023-12-23T13:26:00"),
            amount = 1F,
            description = "A simple transaction",
            type = TransactionType.Expense,
            isPeriodic = false,
            period = TransactionPeriod.None,
            parent = 0,
            category = TransactionCategory.Bank
        )

        fromModel(transaction).`should be equal to`(
            TransactionEntity(
                uid = 1,
                dateTime = LocalDateTime.parse("2023-12-23T13:26:00"),
                amount = 1F,
                description = "A simple transaction",
                type = TransactionType.Expense,
                isPeriodic = false,
                period = TransactionPeriod.None,
                parentId = 0,
                category = TransactionCategory.Bank
            )
        )
    }

    @Test
    fun `Tests toModel method`() {
        TransactionEntity(
            uid = 1,
            dateTime = LocalDateTime.parse("2023-12-23T13:26:00"),
            amount = 1F,
            description = "A simple transaction",
            type = TransactionType.Expense,
            isPeriodic = false,
            period = TransactionPeriod.None,
            parentId = 0,
            category = TransactionCategory.Transport
        ).toModel().`should be equal to`(
            Transaction(
                uid = 1,
                dateTime = LocalDateTime.parse("2023-12-23T13:26:00"),
                amount = 1F,
                description = "A simple transaction",
                type = TransactionType.Expense,
                isPeriodic = false,
                period = TransactionPeriod.None,
                parent = 0,
                category = TransactionCategory.Transport
            )
        )
    }
}
