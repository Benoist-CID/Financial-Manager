package fr.laforge.benoist.repository.implementations.room

import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.laforge.benoist.repository.FinancialRepository
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class AndroidFinancialRepositoryTest : BaseRoomTest() {
    private lateinit var repository: FinancialRepository

    @Before
    fun before() {
        createDatabase()
    }

    @Test
    fun createFinancialInputTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            uid= 1,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted)

        runBlocking {
            repository.getAllIncomes().first().size.shouldBeEqualTo(1)
            repository.getAllIncomes().first()[0].shouldBeEqualTo(toBeInserted)
        }
    }

    @Test
    fun getAllExpensesTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            uid=1,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Expense,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted)
        runBlocking {
            repository.getAllExpenses().first().size.shouldBeEqualTo(1)
            repository.getAllExpenses().first()[0].shouldBeEqualTo(toBeInserted)
        }
    }

    @Test
    fun getAllTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            uid=1,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F, type = TransactionType.Expense,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted)

        val toBeInserted2 = Transaction(
            uid=2,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted2)

        runBlocking {
            val flow = repository.getAll()

            flow.first().size.shouldBeEqualTo(2)
            flow.first()[0].shouldBeEqualTo(toBeInserted)
            flow.first()[1].shouldBeEqualTo(toBeInserted2)
        }
    }

    @Test
    fun getAllPeriodicByTypeTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            uid=1,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F, type = TransactionType.Expense,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted)

        val toBeInserted2 = Transaction(
            uid=2,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted2)

        val toBeInserted3 = Transaction(
            uid=3,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F, type = TransactionType.Expense,
            description = "Description 3",
            isPeriodic = true
        )

        repository.createTransaction(transaction = toBeInserted3)

        val toBeInserted4 = Transaction(
            uid=4,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description 4",
            isPeriodic = true
        )

        repository.createTransaction(transaction = toBeInserted4)

        runBlocking {
            val flow = repository.getAllPeriodicTransactionsByType()

            flow.first().size.shouldBeEqualTo(1)
            flow.first()[0].shouldBeEqualTo(toBeInserted3)
        }
    }

    @Test
    fun deleteTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted)
        repository.createTransaction(transaction = toBeInserted)
        repository.createTransaction(transaction = toBeInserted)

        runBlocking {
            repository.getAll().first().size.shouldBeEqualTo(3)
            val transactionToBeDeleted = repository.getAll().first()[1]
            repository.deleteTransaction(transaction = transactionToBeDeleted)
            repository.getAll().first().size.shouldBeEqualTo(2)
            repository.getAll().first()[0].uid.shouldBeEqualTo(1)
            repository.getAll().first()[1].uid.shouldBeEqualTo(3)
        }
    }

    @Test
    fun getChildrenTransactionsTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            uid=1,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F, type = TransactionType.Expense,
            description = "Description",
            isPeriodic = true,
            period = TransactionPeriod.Monthly
        )

        repository.createTransaction(transaction = toBeInserted)

        val toBeInserted2 = Transaction(
            uid=2,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description"
        )

        repository.createTransaction(transaction = toBeInserted2)

        val toBeInserted3 = Transaction(
            uid=3,
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = TransactionType.Income,
            description = "Description",
            parent = 1
        )

        repository.createTransaction(transaction = toBeInserted3)

        runBlocking {
            val transactions = repository.getChildrenTransactions(
                parentId = 1,
                startDate = LocalDateTime.parse("2023-10-24T00:00:00"),
                endDate = LocalDateTime.parse("2023-11-23T00:00:00")
            )

            transactions.size.shouldBeEqualTo(1)
            transactions[0].shouldBeEqualTo(toBeInserted3)
        }
    }
}
