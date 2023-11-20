package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class TransactionUtilTest {
    @Test
    fun sumTest() {
        val transactionLists = listOf(
            Transaction(amount = 1.0F, type = InputType.Income),
            Transaction(amount = 1.0F, type = InputType.Income),
            Transaction(amount = 1.0F, type = InputType.Income),
            Transaction(amount = 1.0F, type = InputType.Expense),
            Transaction(amount = 1.0F, type = InputType.Income),
            Transaction(amount = 1.0F, type = InputType.Expense),
            Transaction(amount = 1.0F, type = InputType.Income),
            Transaction(amount = 1.0F, type = InputType.Income),
            Transaction(amount = 1.0F, type = InputType.Expense),
            Transaction(amount = 1.0F, type = InputType.Income),
        )

        transactionLists.sum().`should be equal to`(4.0F)
    }
}