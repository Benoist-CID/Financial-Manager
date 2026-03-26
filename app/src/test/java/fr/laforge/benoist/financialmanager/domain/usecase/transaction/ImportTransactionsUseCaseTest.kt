package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class ImportTransactionsUseCaseTest {

    private val createTransactionUseCase = mockk<CreateTransactionUseCase>()
    private val useCase = ImportTransactionsUseCase(createTransactionUseCase)

    // A valid CSV line produced by Transaction.exportToCsvFormat():
    // uid;dateTimeMillis;amount;description;type;isPeriodic;period;parent;category
    private val validLine1 = "1;1700000000000;50.0;Groceries;Expense;false;None;0;Food"
    private val validLine2 = "2;1700100000000;3000.0;Salary;Income;false;None;0;None"

    // --- Happy Path ---

    @Test
    fun `invoke should parse and persist all valid CSV lines`() = runTest {
        // --- Arrange ---
        coEvery { createTransactionUseCase(any()) } returns true
        val csv = "$validLine1\n$validLine2"

        // --- Act ---
        val result = useCase(csv)

        // --- Assert ---
        result shouldBeEqualTo 2
        coVerify(exactly = 2) { createTransactionUseCase(any()) }
    }

    // --- Edge Cases ---

    @Test
    fun `invoke should skip malformed lines and still import valid ones`() = runTest {
        // --- Arrange ---
        coEvery { createTransactionUseCase(any()) } returns true
        val csv = "$validLine1\nTHIS_IS_NOT_VALID\n$validLine2"

        // --- Act ---
        val result = useCase(csv)

        // --- Assert ---
        result shouldBeEqualTo 2
        coVerify(exactly = 2) { createTransactionUseCase(any()) }
    }

    @Test
    fun `invoke with empty string should not call createTransactionUseCase`() = runTest {
        // --- Arrange ---
        // An empty string split by '\n' yields one element: [""].
        // transactionFromCsv("") will throw, so no transactions should be persisted.

        // --- Act ---
        val result = useCase("")

        // --- Assert ---
        result shouldBeEqualTo 0
        coVerify(exactly = 0) { createTransactionUseCase(any()) }
    }
}
