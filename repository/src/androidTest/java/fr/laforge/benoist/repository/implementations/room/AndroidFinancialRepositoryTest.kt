package fr.laforge.benoist.repository.implementations.room

import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.laforge.benoist.repository.FinancialRepository
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType
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
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = InputType.Income,
            description = "Description"
        )

        repository.createFinancialInput(transaction = toBeInserted)

        runBlocking() {
            repository.getAllIncomes().first().size.shouldBeEqualTo(1)
            repository.getAllIncomes().first()[0].shouldBeEqualTo(toBeInserted)
        }
    }

    @Test
    fun getAllExpensesTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = InputType.Expense,
            description = "Description"
        )

        repository.createFinancialInput(transaction = toBeInserted)
        runBlocking {
            repository.getAllExpenses().first().size.shouldBeEqualTo(1)
            repository.getAllExpenses().first()[0].shouldBeEqualTo(toBeInserted)
        }
    }

    @Test
    fun getAllTest() {
        repository = AndroidFinancialRepository(database = db)

        val toBeInserted = Transaction(
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F, type = InputType.Expense,
            description = "Description"
        )

        repository.createFinancialInput(transaction = toBeInserted)

        val toBeInserted2 = Transaction(
            dateTime = LocalDateTime.parse("2023-11-06T00:00:00"),
            amount = 1.0F,
            type = InputType.Income,
            description = "Description"
        )

        repository.createFinancialInput(transaction = toBeInserted2)

        runBlocking {
            val flow = repository.getAll()

            flow.first().size.shouldBeEqualTo(2)
            flow.first()[0].shouldBeEqualTo(toBeInserted)
            flow.first()[1].shouldBeEqualTo(toBeInserted2)
        }
    }
}
