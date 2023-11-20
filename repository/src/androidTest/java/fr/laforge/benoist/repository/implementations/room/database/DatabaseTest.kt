package fr.laforge.benoist.repository.implementations.room.database

import fr.laforge.benoist.model.InputType
import fr.laforge.benoist.repository.implementations.room.BaseRoomTest
import fr.laforge.benoist.repository.implementations.room.entity.FinancialInputEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

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
        val toBeInserted = FinancialInputEntity(
            amount = 1.0F,
            description = "Description",
            type = InputType.Income
        )

        financialInputDao.insertAll(toBeInserted)

        val financialInputs = financialInputDao.getAll()

        runBlocking() {
            financialInputs.first().size.shouldBeEqualTo(1)
            financialInputs.first()[0].apply {
                amount.shouldBeEqualTo(1.0F)
                description.shouldBeEqualTo("Description")
                type.shouldBeEqualTo(InputType.Income)
            }
        }
    }

    @Test
    fun getByInputTypeTest() {
        val toBeInserted1 = FinancialInputEntity(
            amount = 1.0F,
            description = "Description",
            type = InputType.Income
        )

        val toBeInserted2 = FinancialInputEntity(
            amount = 1.0F,
            description = "Description",
            type = InputType.Expense
        )

        financialInputDao.insertAll(toBeInserted1, toBeInserted2)
        runBlocking() {
            financialInputDao.getByInputType(inputType = InputType.Income).first().size.shouldBeEqualTo(1)
            val read1 = financialInputDao.getByInputType(inputType = InputType.Income).first()[0]
            read1.type.shouldBeEqualTo(toBeInserted1.type)
            read1.dateTime.shouldBeEqualTo(toBeInserted1.dateTime)
            read1.amount.shouldBeEqualTo(toBeInserted1.amount)
            read1.description.shouldBeEqualTo(toBeInserted1.description)
            financialInputDao.getByInputType(inputType = InputType.Expense).first().size.shouldBeEqualTo(1)
            val read2 = financialInputDao.getByInputType(inputType = InputType.Expense).first()[0]
            read2.type.shouldBeEqualTo(toBeInserted2.type)
            read2.dateTime.shouldBeEqualTo(toBeInserted2.dateTime)
            read2.amount.shouldBeEqualTo(toBeInserted2.amount)
            read2.description.shouldBeEqualTo(toBeInserted2.description)
        }
    }
}
