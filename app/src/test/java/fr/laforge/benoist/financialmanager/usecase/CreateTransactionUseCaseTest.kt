package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class CreateTransactionUseCaseTest : KoinTest {
    private val mockRepository by lazy {
        createMockRepository()
    }
    private val createTransactionUseCase: CreateTransactionUseCase by inject()

    @Before
    fun before() {
        startKoin {
            modules(
                module {
                    single { mockRepository }
                    single<CreateTransactionUseCase> {CreateTransactionUseCaseImpl()}
                }
            )
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `Tests CreateTransactionUseCase with a non periodic command`()  {
        val testDate = LocalDateTime.now()
        val transaction = Transaction(
            dateTime = testDate,
            type = TransactionType.Expense,
            amount = 150F,
            description = "A simple non-periodic transaction"
        )

        runBlocking {
            createTransactionUseCase.execute(
                transaction
            ).first()

            verify(mockRepository, times(1)).createTransaction(transaction)
            verify(mockRepository, times(0)).createTransaction(transaction.copy(isPeriodic = false, parent = 1))
        }
    }

    @Test
    fun `Tests CreateTransactionUseCase with a periodic command`()  {
        val testDate = LocalDateTime.now()
        val transaction = Transaction(
            dateTime = testDate,
            type = TransactionType.Expense,
            amount = 150F,
            description = "A simple periodic transaction",
            isPeriodic = true
        )

        runBlocking {
            createTransactionUseCase.execute(
                transaction
            ).first()

            verify(mockRepository, times(1)).createTransaction(transaction)
            verify(mockRepository, times(1)).createTransaction(transaction.copy(isPeriodic = false, parent = 1))
        }
    }

    private fun createMockRepository(): FinancialRepository {
        val mockRepository: FinancialRepository = Mockito.mock()

        Mockito.`when`(mockRepository.createTransaction(any())).thenReturn(1)

        return mockRepository
    }

//    @Test
//    fun `Tests CreateTransactionUseCase with a periodic command`() {
//        val testDate = LocalDateTime.parse("2024-01-01T00:00:00")
//        val transactions = CreateTransactionUseCaseImpl().execute(
//            date = testDate,
//            transactionType = TransactionType.Expense,
//            amount = 150F,
//            description = "A periodic transaction",
//            isPeriodic = true,
//            startDate = testDate,
//            endDate = testDate.plusMonths(11)
//        )
//
//        transactions.size.`should be equal to`(12)
//
//        transactions.`should be equal to`(
//            listOf(
//                Transaction(
//                    dateTime = testDate.plusMonths(11),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(10),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),Transaction(
//                    dateTime = testDate.plusMonths(9),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ), Transaction(
//                    dateTime = testDate.plusMonths(8),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(7),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(6),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(5),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(4),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(3),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(2),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate.plusMonths(1),
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                ),
//                Transaction(
//                    dateTime = testDate,
//                    type = TransactionType.Expense,
//                    amount = 150F,
//                    description = "A periodic transaction"
//                )
//            )
//        )
//    }
}