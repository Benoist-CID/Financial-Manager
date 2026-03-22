package fr.laforge.benoist.financialmanager.infrastructure.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.FinancialInputDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.database.AppDatabase
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class FinancialDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: FinancialInputDao

    @Before
    fun createDb() {
        stopKoin()
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.getFinancialInputDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        stopKoin()
    }

    @Test
    fun `insertAll should return a list of the inserted indexes`() = runTest {
        // --- Arrange ---
        val transaction = createTransaction(uid = 1)

        // --- Act ---
        val indexes = dao.insertAll(listOf(transaction))

        // --- Assert ---
        indexes.size `should be equal to` 1
        indexes[0] `should be equal to` 1L
    }

    @Test
    fun `getAll should return a list of the inserted transactions`() = runTest {
        // --- Arrange ---
        val transaction = createTransaction(
            uid = 1,
            dateString = "2026-01-03T00:00:00"
        )
        dao.insertAll(listOf(transaction))

        // --- Act ---
        val transactions = dao.getAll().first()

        // --- Assert ---
        transactions.size `should be equal to` 1
        transactions[0] `should be equal to` transaction
    }

    @Test
    fun `getBalanceBefore should calculate balance correctly ignoring periodic and future transactions`() = runTest {
        // --- Arrange ---
        // We want the balance strictly BEFORE Feb 1st
        val cutoffDate = LocalDateTime.parse("2026-02-01T00:00:00")

        val transactions = listOf(
            // 1. PAST INCOME: Should be counted (+1000)
            createTransaction(
                uid = 1,
                amount = 1000.0f,
                type = TransactionType.Income,
                dateString = "2026-01-01T10:00:00",
                isPeriodic = false
            ),

            // 2. PAST EXPENSE: Should be counted (-200)
            createTransaction(
                uid = 2,
                amount = 200.0f,
                type = TransactionType.Expense,
                dateString = "2026-01-15T10:00:00",
                isPeriodic = false
            ),

            // 3. PERIODIC RULE: Should be IGNORED even if date is valid
            createTransaction(
                uid = 3,
                amount = 5000.0f,
                type = TransactionType.Income,
                dateString = "2026-01-01T10:00:00",
                isPeriodic = true
            ),

            // 4. FUTURE TRANSACTION: Should be IGNORED (Date >= Cutoff)
            createTransaction(
                uid = 4,
                amount = 50.0f,
                type = TransactionType.Expense,
                dateString = "2026-02-02T10:00:00", // After cutoff
                isPeriodic = false
            )
        )

        dao.insertAll(transactions)

        // --- Act ---
        val balance = dao.getBalanceBefore(cutoffDate).first()

        // --- Assert ---
        // Calculation: 1000 (Income) - 200 (Expense) = 800.0
        // Items 3 and 4 are ignored.
        balance `should be equal to` 800.0f
    }

    @Test
    fun `getTransactions should return all items when all filters are null`() = runTest {
        // --- Arrange ---
        val t1 = createTransaction(uid = 1, description = "A")
        val t2 = createTransaction(uid = 2, description = "B")
        dao.insertAll(listOf(t1, t2))

        // --- Act ---
        // Passing nulls for everything
        val result = dao.getTransactions(
            null, null, null, null, null,
            parentId = 0
        ).first()

        // --- Assert ---
        result.size `should be equal to` 2
    }

    @Test
    fun `getTransactions should filter by Type correctly`() = runTest {
        // --- Arrange ---
        val expense = createTransaction(uid = 1, type = TransactionType.Expense)
        val income = createTransaction(uid = 2, type = TransactionType.Income)
        dao.insertAll(listOf(expense, income))

        // --- Act ---
        // Note: Assuming your DB stores the enum name as a String.
        // If it stores Ordinals, you might need to adjust this depending on your TypeConverter.
        val result = dao.getTransactions(
            type = TransactionType.Expense.toString(),
            startDate = null, endDate = null, search = null, isPeriodic = null,
            parentId = 0
        ).first()

        // --- Assert ---
        result.size `should be equal to` 1
        result[0].type `should be equal to` TransactionType.Expense
    }

    @Test
    fun `getTransactions should filter by Date Range inclusive`() = runTest {
        // --- Arrange ---
        val t1 = createTransaction(uid = 1, dateString = "2026-01-01T10:00:00") // Inside
        val t2 = createTransaction(uid = 2, dateString = "2026-01-15T10:00:00") // Inside
        val t3 = createTransaction(uid = 3, dateString = "2026-02-01T10:00:00") // Outside (Too late)
        val t4 = createTransaction(uid = 4, dateString = "2025-12-31T23:59:59") // Outside (Too early)
        dao.insertAll(listOf(t1, t2, t3, t4))

        // --- Act ---
        val result = dao.getTransactions(
            type = null,
            startDate = LocalDateTime.parse("2026-01-01T00:00:00"),
            endDate = LocalDateTime.parse("2026-01-31T23:59:59"),
            search = null,
            isPeriodic = null,
            parentId = 0
        ).first()

        // --- Assert ---
        result.size `should be equal to` 2
        result.map { it.uid } `should be equal to` listOf(1, 2)
    }

    @Test
    fun `getTransactions should filter by Description search (LIKE)`() = runTest {
        // --- Arrange ---
        val t1 = createTransaction(uid = 1, description = "Carrefour Market")
        val t2 = createTransaction(uid = 2, description = "Auchan Supermarket")
        val t3 = createTransaction(uid = 3, description = "Total Gas Station")
        dao.insertAll(listOf(t1, t2, t3))

        // --- Act ---
        // Should find "Carrefour Market" and "Auchan Supermarket" because they contain "Market"
        val result = dao.getTransactions(
            type = null, startDate = null, endDate = null,
            search = "Market",
            isPeriodic = null,
            parentId = 0
        ).first()

        // --- Assert ---
        result.size `should be equal to` 2
        result.map { it.description } `should be equal to` listOf("Carrefour Market", "Auchan Supermarket")
    }

    @Test
    fun `getTransactions should filter by isPeriodic`() = runTest {
        // --- Arrange ---
        val realTx = createTransaction(uid = 1, isPeriodic = false)
        val templateTx = createTransaction(uid = 2, isPeriodic = true)
        dao.insertAll(listOf(realTx, templateTx))

        // --- Act ---
        val result = dao.getTransactions(
            type = null, startDate = null, endDate = null, search = null,
            isPeriodic = false,
            parentId = 0
        ).first()

        // --- Assert ---
        result.size `should be equal to` 1
        result[0].isPeriodic `should be equal to` false
    }

    @Test
    fun `getTransactions should apply multiple filters simultaneously (AND logic)`() = runTest {
        // --- Arrange ---
        val target = createTransaction(
            uid = 1,
            description = "Target Transaction",
            type = TransactionType.Expense,
            dateString = "2026-01-10T10:00:00",
            isPeriodic = false
        )

        // Decoy 1: Wrong Type
        val decoy1 = createTransaction(uid = 2, description = "Target Transaction", type = TransactionType.Income, dateString = "2026-01-10T10:00:00", isPeriodic = false)

        // Decoy 2: Wrong Date
        val decoy2 = createTransaction(uid = 3, description = "Target Transaction", type = TransactionType.Expense, dateString = "2026-02-10T10:00:00", isPeriodic = false)

        // Decoy 3: Wrong Description
        val decoy3 = createTransaction(uid = 4, description = "Hidden Item", type = TransactionType.Expense, dateString = "2026-01-10T10:00:00", isPeriodic = false)

        // Decoy 4: Wrong Periodic
        val decoy4 = createTransaction(uid = 5, description = "Target Transaction", type = TransactionType.Expense, dateString = "2026-01-10T10:00:00", isPeriodic = true)

        dao.insertAll(listOf(target, decoy1, decoy2, decoy3, decoy4))

        // --- Act ---
        val result = dao.getTransactions(
            type = TransactionType.Expense.toString(),
            startDate = LocalDateTime.parse("2026-01-01T00:00:00"),
            endDate = LocalDateTime.parse("2026-01-31T23:59:59"),
            search = "Target",
            isPeriodic = false,
            parentId = 0
        ).first()

        // --- Assert ---
        result.size `should be equal to` 1
        result[0] `should be equal to` target
    }

    // --- Helper Method to reduce boilerplate ---
    private fun createTransaction(
        uid: Int,
        dateString: String = "2026-01-01T00:00:00",
        amount: Float = 150.0f,
        type: TransactionType = TransactionType.Expense,
        isPeriodic: Boolean = false,
        description: String = "Test Description"
    ): TransactionEntity {
        return TransactionEntity(
            uid = uid, // Assuming Entity uses Long, user snippet had Int but DAO returns List<Long>
            dateTime = LocalDateTime.parse(dateString),
            amount = amount,
            type = type,
            description = description,
            isPeriodic = isPeriodic,
            period = TransactionPeriod.None,
            parentId = 0,
            category = TransactionCategory.Transport
        )
    }
}
