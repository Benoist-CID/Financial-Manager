package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test

class CreateTransactionUseCaseTest {
    private val repository: FinancialRepository = mockk()
    private lateinit var useCase: CreateTransactionUseCase

    @Before
    fun setUp() {
        useCase = CreateTransactionUseCaseImpl(repository)
    }

    @Test
    fun `When transaction is NOT periodic, creates one transaction`() = runBlocking {
        // Arrange
        val transaction = Transaction(isPeriodic = false)
        coEvery { repository.createTransaction(any()) } returns 1L

        // Act
        val result = useCase(transaction)

        // Assert
        result `should be equal to` true
        coVerify(exactly = 1) { repository.createTransaction(transaction) }
    }

    @Test
    fun `When transaction is periodic, creates two transactions`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 0, isPeriodic = true)
        val parentId = 1L
        coEvery { repository.createTransaction(transaction) } returns parentId
        coEvery { repository.createTransaction(match { it.parent == parentId.toInt() }) } returns 2L

        // Act
        val result = useCase(transaction)

        // Assert
        result `should be equal to` true
        coVerify(exactly = 1) { repository.createTransaction(transaction) }
        coVerify(exactly = 1) {
            repository.createTransaction(match {
                it.parent == parentId.toInt() && !it.isPeriodic
            })
        }
    }
}
