package fr.laforge.benoist.repository.implementations.room.database

import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.implementations.room.BaseRoomTest
import fr.laforge.benoist.repository.implementations.room.entity.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class DatabaseTest : BaseRoomTest() {
    private val financialInputDao by lazy {
        db.getFinancialInputDao()
    }

    @Before
    fun before() {
        createDatabase()
    }

    @Test
    fun insertTest() {
        val toBeInserted = TransactionEntity(
            amount = 1.0F,
            description = "Description",
            type = TransactionType.Income,
            isPeriodic = false,
            period = TransactionPeriod.None
        )

        financialInputDao.insertAll(toBeInserted)

        val financialInputs = financialInputDao.getAll()

        runBlocking {
            financialInputs.first().size.shouldBeEqualTo(1)
            financialInputs.first()[0].apply {
                amount.shouldBeEqualTo(1.0F)
                description.shouldBeEqualTo("Description")
                type.shouldBeEqualTo(TransactionType.Income)
            }
        }
    }

    @Test
    fun getTransactionByIdTest() {
        val toBeInserted1 = TransactionEntity(
            amount = 1.0F,
            description = "Description",
            type = TransactionType.Income,
            isPeriodic = false,
            period = TransactionPeriod.None
        )

        val toBeInserted2 = TransactionEntity(
            amount = 1.0F,
            description = "Description",
            type = TransactionType.Expense,
            isPeriodic = false,
            period = TransactionPeriod.None
        )

        financialInputDao.insertAll(toBeInserted1, toBeInserted2)

        runBlocking {
            val toBeTested1 = TransactionEntity(
                uid = 1,
                dateTime = toBeInserted1.dateTime,
                amount = 1.0F,
                description = "Description",
                type = TransactionType.Income,
                isPeriodic = false,
                period = TransactionPeriod.None
            )

            financialInputDao.getById(uid = 1).first().shouldBeEqualTo(toBeTested1)

            val toBeTested2 = TransactionEntity(
                uid = 2,
                dateTime = toBeInserted2.dateTime,
                amount = 1.0F,
                description = "Description",
                type = TransactionType.Expense,
                isPeriodic = false,
                period = TransactionPeriod.None
            )

            financialInputDao.getById(uid = 2).first().shouldBeEqualTo(toBeTested2)
        }
    }

    @Test
    fun getByInputTypeTest() {
        val toBeInserted1 = TransactionEntity(
            amount = 1.0F,
            description = "Description",
            type = TransactionType.Income,
            isPeriodic = false,
            period = TransactionPeriod.None
        )

        val toBeInserted2 = TransactionEntity(
            amount = 1.0F,
            description = "Description",
            type = TransactionType.Expense,
            isPeriodic = false,
            period = TransactionPeriod.None
        )

        financialInputDao.insertAll(toBeInserted1, toBeInserted2)
        runBlocking {
            financialInputDao.getByInputType(inputType = TransactionType.Income).first().size.shouldBeEqualTo(1)
            val read1 = financialInputDao.getByInputType(inputType = TransactionType.Income).first()[0]
            read1.type.shouldBeEqualTo(toBeInserted1.type)
            read1.dateTime.shouldBeEqualTo(toBeInserted1.dateTime)
            read1.amount.shouldBeEqualTo(toBeInserted1.amount)
            read1.description.shouldBeEqualTo(toBeInserted1.description)
            financialInputDao.getByInputType(inputType = TransactionType.Expense).first().size.shouldBeEqualTo(1)
            val read2 = financialInputDao.getByInputType(inputType = TransactionType.Expense).first()[0]
            read2.type.shouldBeEqualTo(toBeInserted2.type)
            read2.dateTime.shouldBeEqualTo(toBeInserted2.dateTime)
            read2.amount.shouldBeEqualTo(toBeInserted2.amount)
            read2.description.shouldBeEqualTo(toBeInserted2.description)
        }
    }

    @Test
    fun getAllInDateRangeTest() {
        val periodicTransactionStartDate = LocalDateTime.parse("2023-01-01T00:00:00")
        val testDate = LocalDateTime.parse("2024-01-01T00:00:00")
        val transactions = listOf(
            TransactionEntity(
                dateTime = periodicTransactionStartDate,
                type = TransactionType.Expense,
                amount = 150F,
                description = "A periodic transaction",
                isPeriodic = true,
                period = TransactionPeriod.Monthly
            ),
            TransactionEntity(
                dateTime = testDate,
                type = TransactionType.Expense,
                amount = 150F,
                description = "A non periodic transaction",
                isPeriodic = false,
                period = TransactionPeriod.None
            ),
            TransactionEntity(
                dateTime = testDate.plusMonths(1),
                type = TransactionType.Expense,
                amount = 150F,
                description = "A non periodic transaction",
                isPeriodic = false,
                period = TransactionPeriod.None
            ),
            TransactionEntity(
                dateTime = testDate.plusMonths(2),
                type = TransactionType.Expense,
                amount = 150F,
                description = "A periodic transaction",
                isPeriodic = false,
                period = TransactionPeriod.None
            )
        )

        transactions.forEach {
            financialInputDao.insertAll(it)
        }

        runBlocking {
            val entities = financialInputDao.getAllInDateRange(
                startDate = LocalDateTime.parse("2024-01-31T00:00:00"),
                endDate = LocalDateTime.parse("2024-12-01T00:00:00")
            ).first()

            entities.size.shouldBeEqualTo(3)
            entities[0].dateTime.shouldBeEqualTo(transactions[3].dateTime)
            entities[1].dateTime.shouldBeEqualTo(transactions[2].dateTime)
            entities[2].dateTime.shouldBeEqualTo(transactions[0].dateTime)
        }
    }

    @Test
    fun getAllPeriodicTest() {
        val transactions = listOf(
            TransactionEntity(
                type = TransactionType.Expense,
                amount = 1F,
                description = "A periodic transaction",
                isPeriodic = true,
                period = TransactionPeriod.Monthly
            ),
            TransactionEntity(
                type = TransactionType.Expense,
                amount = 150F,
                description = "A non periodic transaction",
                isPeriodic = false,
                period = TransactionPeriod.None
            ),
            TransactionEntity(
                type = TransactionType.Expense,
                amount = 150F,
                description = "A non periodic transaction",
                isPeriodic = false,
                period = TransactionPeriod.None
            ),
            TransactionEntity(
                type = TransactionType.Expense,
                amount = 2F,
                description = "A periodic transaction",
                isPeriodic = true,
                period = TransactionPeriod.None
            ),
            TransactionEntity(
                type = TransactionType.Income,
                amount = 3F,
                description = "A periodic transaction",
                isPeriodic = true,
                period = TransactionPeriod.None
            )
        )

        transactions.forEach {
            financialInputDao.insertAll(it)
        }

        runBlocking {
            val entities = financialInputDao.getAllPeriodic().first()

            entities.size.shouldBeEqualTo(2)
            entities[0].isPeriodic.shouldBeEqualTo(true)
            entities[0].amount.shouldBeEqualTo(1F)
            entities[1].isPeriodic.shouldBeEqualTo(true)
            entities[1].amount.shouldBeEqualTo(2F)
        }
    }
}
