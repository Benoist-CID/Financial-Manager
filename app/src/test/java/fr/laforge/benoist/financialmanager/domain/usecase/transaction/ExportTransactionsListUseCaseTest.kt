package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContain
import org.junit.Test
import java.time.LocalDateTime

class ExportTransactionsListUseCaseTest {

    private val useCase = ExportTransactionsListUseCase()

    @Test
    fun `invoke should return success when list is not empty`() {
        // Arrange
        val transactions = listOf(
            Transaction(uid = 1, amount = 10f, description = "Test 1", type = TransactionType.Expense),
            Transaction(uid = 2, amount = 20f, description = "Test 2", type = TransactionType.Income)
        )

        // Act
        val result = useCase(transactions)

        // Assert
        result.isSuccess shouldBeEqualTo true
        val csv = result.getOrNull()!!
        csv shouldContain "Test 1"
        csv shouldContain "Test 2"
        csv.lines().filter { it.isNotBlank() }.size shouldBeEqualTo 2
    }

    @Test
    fun `invoke should return failure when list is empty`() {
        // Arrange
        val transactions = emptyList<Transaction>()

        // Act
        val result = useCase(transactions)

        // Assert
        result.isFailure shouldBeEqualTo true
        result.exceptionOrNull() shouldBeInstanceOf IllegalArgumentException::class
    }
}
