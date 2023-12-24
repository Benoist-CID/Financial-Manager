package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.LocalDateTime

class CreateRegularTransactionsUseCaseTest : KoinTest {
    private val createRegularTransactionsUseCase: CreateRegularTransactionsUseCase by inject()
    private val mockRepository: FinancialRepository by lazy {
        createMockRepository()
    }

    @Before
    fun before() {
        startKoin {
            modules(
                module {
                    single { mockRepository }
                    single<CreateRegularTransactionsUseCase> {
                        CreateRegularTransactionsUseCaseImpl()
                    }
                }
            )
        }
    }

    @After
    fun after() {
        stopKoin()
    }


    @Test
    fun `Tests CreateRegularTransactionsUseCase with regular dates`() {
        runBlocking {
            createRegularTransactionsUseCase.execute(
                startDate = START_DATE_1,
                endDate = END_DATE_1,
                currentDate = CURRENT_DATE_1
            ).first()

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2023-12-20T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 1
                )
            )

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2023-11-27T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 2
                )
            )
        }
    }

    @Test
    fun `Tests CreateRegularTransactionsUseCase with date in december and january`() {
        runBlocking {
            createRegularTransactionsUseCase.execute(
                startDate = START_DATE_2,
                endDate = END_DATE_2,
                currentDate = CURRENT_DATE_2
            ).first()

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2024-01-20T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 1
                )
            )

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2023-12-27T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 2
                )
            )
        }
    }

    @Test
    fun `Tests CreateRegularTransactionsUseCase with date in december and january 2`() {
        runBlocking {
            createRegularTransactionsUseCase.execute(
                startDate = START_DATE_2,
                endDate = END_DATE_2,
                currentDate = CURRENT_DATE_2
            ).first()

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2024-01-20T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 1
                )
            )

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2023-12-27T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 2
                )
            )
        }
    }

    @Test
    fun `Tests CreateRegularTransactionsUseCase with date in december and january 3`() {
        runBlocking {
            createRegularTransactionsUseCase.execute(
                startDate = START_DATE_2,
                endDate = END_DATE_2,
                currentDate = CURRENT_DATE_3
            ).first()

            verify(mockRepository, times(1)).createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2024-01-05T00:00:00"),
                    amount = 1F,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 3
                )
            )
        }
    }

    private fun createMockRepository(): FinancialRepository {
        val mockRepository: FinancialRepository = Mockito.mock()

        Mockito.`when`(mockRepository.getAllPeriodicTransactionsByType()).thenReturn(
            flowOf(
                getPeriodicTransactions()
            )
        )

        return mockRepository
    }

    private fun getPeriodicTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                uid = 1,
                dateTime = CREATION_DATE,
                amount = 1F,
                description = "A periodic income",
                type = TransactionType.Income,
                isPeriodic = true,
                period = TransactionPeriod.Monthly,
            ),
            Transaction(
                uid = 2,
                dateTime = CREATION_DATE_2,
                amount = 1F,
                description = "A periodic income",
                type = TransactionType.Income,
                isPeriodic = true,
                period = TransactionPeriod.Monthly,
            ),
            Transaction(
                uid = 3,
                dateTime = CREATION_DATE_3,
                amount = 1F,
                description = "A periodic income",
                type = TransactionType.Income,
                isPeriodic = true,
                period = TransactionPeriod.Monthly,
            )
        )
    }

    companion object {
        val CREATION_DATE: LocalDateTime = LocalDateTime.parse("2022-12-20T00:00:00")
        val CREATION_DATE_2: LocalDateTime = LocalDateTime.parse("2022-11-27T00:00:00")
        val CREATION_DATE_3: LocalDateTime = LocalDateTime.parse("2022-11-05T00:00:00")
        val START_DATE_1: LocalDateTime = LocalDateTime.parse("2023-11-24T00:00:00")
        val END_DATE_1: LocalDateTime = LocalDateTime.parse("2023-12-24T00:00:00")
        val CURRENT_DATE_1: LocalDateTime = LocalDateTime.parse("2023-12-10T00:00:00")
        val START_DATE_2: LocalDateTime = LocalDateTime.parse("2023-12-24T00:00:00")
        val END_DATE_2: LocalDateTime = LocalDateTime.parse("2024-01-24T00:00:00")
        val CURRENT_DATE_2: LocalDateTime = LocalDateTime.parse("2024-01-10T00:00:00")
        val CURRENT_DATE_3: LocalDateTime = LocalDateTime.parse("2023-12-30T00:00:00")
    }
}