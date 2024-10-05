import fr.laforge.benoist.financialmanager.domain.usecases.DeletePeriodicStatus
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCaseImpl
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeleteTransactionUseCaseImplTest {

    private val onPeriodicChildTransaction: () -> DeletePeriodicStatus = mock()
    private val onShouldDisplayConfirmationDialog: () -> Boolean = mock()

    @Test
    fun `invoke should delete single transaction when confirmed`() = runBlocking {
        val transaction = Transaction(uid = 1, parent = 0)
        val repository = mock<FinancialRepository>()

        whenever(onShouldDisplayConfirmationDialog()).thenReturn(true)

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result =
            useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        verify(repository).deleteTransaction(transaction)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `invoke should delete recurring transaction and children when confirmed`() = runBlocking {

        val transaction = Transaction(uid = 1, parent = 2)
        val parentTransaction = Transaction(uid = 2)
        val repository = mock<FinancialRepository>()

        whenever(onPeriodicChildTransaction()).thenReturn(DeletePeriodicStatus.AllOccurrences)
        whenever(onShouldDisplayConfirmationDialog()).thenReturn(true)
        whenever(repository.get(uid = 2)).thenReturn(flowOf(parentTransaction))

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result =
            useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        verify(repository, times(1)).deleteTransaction(transaction)
        verify(repository, times(1)).deleteTransaction(parentTransaction)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `invoke should delete only current occurrence of recurring transaction when confirmed`() = runBlocking {
        val transaction = Transaction(uid = 1, parent = 2)
        val repository = mock<FinancialRepository>()

        whenever(onPeriodicChildTransaction()).thenReturn(DeletePeriodicStatus.ThisOccurrenceOnly)
        whenever(onShouldDisplayConfirmationDialog()).thenReturn(true)

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result = useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        verify(repository).deleteTransaction(transaction)
        verify(repository, never()).get(any()) // Parent should not be fetched
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `invoke should not delete transaction when confirmation is canceled`() = runBlocking {
        val transaction = Transaction(uid = 1)
        val repository = mock<FinancialRepository>()

        whenever(onShouldDisplayConfirmationDialog()).thenReturn(false)

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result = useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        verify(repository, never()).deleteTransaction(any())
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == false)
    }

    @Test
    fun `invoke should return failure when deleting transaction throws exception`() = runBlocking {
        val transaction = Transaction(uid = 1)
        val repository = mock<FinancialRepository>()

        whenever(onShouldDisplayConfirmationDialog()).thenReturn(true)
        whenever(repository.deleteTransaction(transaction)).thenThrow(RuntimeException("Error"))

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result = useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `invoke should return failure when deleting parent transaction throws exception`() = runBlocking {
        val transaction = Transaction(uid = 1, parent = 2)
        val parentTransaction = Transaction(uid = 2)
        val repository = mock<FinancialRepository>()

        whenever(onPeriodicChildTransaction()).thenReturn(DeletePeriodicStatus.AllOccurrences)
        whenever(onShouldDisplayConfirmationDialog()).thenReturn(true)
        whenever(repository.get(uid = 2)).thenReturn(flowOf(parentTransaction))
        whenever(repository.deleteTransaction(parentTransaction)).thenThrow(RuntimeException("Error"))

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result = useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `invoke should return failure when fetching parent transaction throws exception`() = runBlocking {
        val transaction = Transaction(uid = 1, parent = 2)
        val repository = mock<FinancialRepository>()

        whenever(onPeriodicChildTransaction()).thenReturn(DeletePeriodicStatus.AllOccurrences)
        whenever(onShouldDisplayConfirmationDialog()).thenReturn(true)
        whenever(repository.get(uid = 2)).thenThrow(RuntimeException("Error"))

        val useCase = DeleteTransactionUseCaseImpl(repository)
        val result = useCase(transaction, onPeriodicChildTransaction, onShouldDisplayConfirmationDialog)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}