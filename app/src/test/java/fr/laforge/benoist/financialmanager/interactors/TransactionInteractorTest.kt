package fr.laforge.benoist.financialmanager.interactors

import fr.laforge.benoist.financialmanager.domain.usecases.CheckIfTransactionIsPeriodicUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.UpdateTransactionUseCase
import fr.laforge.benoist.model.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class TransactionInteractorTest {

    private val checkIfTransactionIsPeriodicUseCase: CheckIfTransactionIsPeriodicUseCase = mock()
    private val deleteTransactionUseCase: DeleteTransactionUseCase = mock()
    private val updateTransactionUseCase: UpdateTransactionUseCase = mock()

    private val transactionInteractor: TransactionInteractor =
        TransactionInteractorImpl(
            checkIfTransactionIsPeriodicUseCase =  checkIfTransactionIsPeriodicUseCase,
            deleteTransactionUseCase = deleteTransactionUseCase,
            updateTransactionUseCase = updateTransactionUseCase
        )

    @Test
    fun `isPeriodicTransaction should call checkIfTransactionIsPeriodicUseCase and return its result`() {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        `when`(checkIfTransactionIsPeriodicUseCase(transaction)).thenReturn(true)

        // Act
        val result = transactionInteractor.isPeriodicTransaction(transaction)

        // Assert
        verify(checkIfTransactionIsPeriodicUseCase).invoke(transaction)
        assertEquals(true, result)
    }

    @Test
    fun `deleteTransaction should call deleteTransactionUseCase and return its result`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        val expectedResult = Result.success(true)
        `when`(deleteTransactionUseCase(transaction, DeleteTransactionType.ThisOccurrenceOnly))
            .thenReturn(expectedResult)

        // Act
        val result =
            transactionInteractor.deleteTransaction(transaction, DeleteTransactionType.ThisOccurrenceOnly)

        // Assert
        verify(deleteTransactionUseCase).invoke(transaction, DeleteTransactionType.ThisOccurrenceOnly)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `updateTransaction should call updateTransactionUseCase and return its result`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        val expectedResult = Result.success(true)
        `when`(updateTransactionUseCase(transaction, shouldUpdateParent = false)).thenReturn(expectedResult)

        // Act
        val result = transactionInteractor.updateTransaction(transaction, shouldUpdateParent = false)

        // Assert
        verify(updateTransactionUseCase).invoke(transaction, shouldUpdateParent = false)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `updateTransaction should call updateTransactionUseCase with shouldUpdateParent true and return its result`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 2)
        val expectedResult = Result.success(true)
        `when`(updateTransactionUseCase(transaction, shouldUpdateParent = true)).thenReturn(expectedResult)

        // Act
        val result = transactionInteractor.updateTransaction(transaction, shouldUpdateParent = true)

        // Assert
        verify(updateTransactionUseCase).invoke(transaction, shouldUpdateParent = true)
        assertEquals(expectedResult, result)
    }
}
