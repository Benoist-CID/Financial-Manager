package fr.laforge.benoist.financialmanager.domain.usecases

import androidx.compose.ui.input.key.type
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class UpdateTransactionUseCaseImplTest {

    private val financialRepository: FinancialRepository = mock(FinancialRepository::class.java)
    private val updateTransactionUseCase: UpdateTransactionUseCase =
        UpdateTransactionUseCaseImpl(financialRepository)

    @Test
    fun `invoke should update transaction and return success when shouldUpdateParent is false`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        doNothing().`when`(financialRepository).updateTransaction(transaction)

        // Act
        val result = updateTransactionUseCase(transaction, shouldUpdateParent = false)

        // Assert
        verify(financialRepository).updateTransaction(transaction)
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `invoke should update transaction and parent transaction and return success when shouldUpdateParent is true`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 2, type = TransactionType.Income, category = TransactionCategory.Food, amount = 10.0F, description = "Lunch")
        val parentTransaction = Transaction(uid = 2, parent = 0)
        doNothing().`when`(financialRepository).updateTransaction(any())
        `when`(financialRepository.get(uid = transaction.parent)).thenReturn(flowOf(parentTransaction))

        // Act
        val result = updateTransactionUseCase(transaction, shouldUpdateParent = true)

        // Assert
        verify(financialRepository).updateTransaction(transaction)
        verify(financialRepository).updateTransaction(
            argThat { t ->
                t.uid == parentTransaction.uid &&
                        t.type == transaction.type &&
                        t.category == transaction.category &&
                        t.amount == transaction.amount &&
                        t.description == transaction.description
            }
        )
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `invoke should handle exception during transaction update and return failure`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        val exception = RuntimeException("Something went wrong")
        doThrow(exception).`when`(financialRepository).updateTransaction(transaction)

        // Act
        val result = updateTransactionUseCase(transaction, shouldUpdateParent = false)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle exception during parent transaction update and return failure`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 2)
        val parentTransaction = Transaction(uid = 2, parent = 0)
        val exception = RuntimeException("Something went wrong")
        doNothing().`when`(financialRepository).updateTransaction(transaction)
        `when`(financialRepository.get(uid = transaction.parent)).thenReturn(flowOf(parentTransaction))
        doThrow(exception).`when`(financialRepository).updateTransaction(parentTransaction)

        // Act
        val result = updateTransactionUseCase(transaction, shouldUpdateParent = true)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}