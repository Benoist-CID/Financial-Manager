package fr.laforge.benoist.financialmanager.interactors

import fr.laforge.benoist.financialmanager.domain.usecases.CheckIfTransactionIsPeriodicUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCase
import fr.laforge.benoist.model.Transaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class DeleteTransactionInteractorTest {

    private val checkIfTransactionIsPeriodicUseCase: CheckIfTransactionIsPeriodicUseCase = mock()
    private val deleteTransactionUseCase: DeleteTransactionUseCase = mock()
    private val deleteTransactionInteractor: DeleteTransactionInteractor =
        DeleteTransactionInteractorImpl(
            checkIfTransactionIsPeriodicUseCase,
            deleteTransactionUseCase
        )

    @Test
    fun `isPeriodicTransaction should call checkIfTransactionIsPeriodicUseCase and return its result`() {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        `when`(checkIfTransactionIsPeriodicUseCase(transaction)).thenReturn(true)

        // Act
        val result = deleteTransactionInteractor.isPeriodicTransaction(transaction)

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
            deleteTransactionInteractor.deleteTransaction(transaction, DeleteTransactionType.ThisOccurrenceOnly)

        // Assert
        verify(deleteTransactionUseCase).invoke(transaction, DeleteTransactionType.ThisOccurrenceOnly)
        assertEquals(expectedResult, result)
    }
}
