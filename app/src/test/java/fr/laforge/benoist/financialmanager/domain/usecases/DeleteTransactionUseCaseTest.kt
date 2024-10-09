
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCaseImpl
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class DeleteTransactionUseCaseTest {

    private val financialRepository: FinancialRepository = Mockito.mock(FinancialRepository::class.java)
    private val deleteTransactionUseCase: DeleteTransactionUseCase =
        DeleteTransactionUseCaseImpl(financialRepository)

    @Test
    fun `invoke should delete transaction and return success for ThisOccurrenceOnly`() = runBlocking {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)
        doNothing().`when`(financialRepository).deleteTransaction(transaction)

        // Act
        val result = deleteTransactionUseCase(transaction, DeleteTransactionType.ThisOccurrenceOnly)

        // Assert
        verify(financialRepository).deleteTransaction(transaction)
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `invoke should delete transaction and parent transaction and return success for AllOccurrences`() =
        runBlocking {
            // Arrange
            val transaction = Transaction(uid = 1, parent = 2)
            val parentTransaction = Transaction(uid = 2, parent = 0)
            doNothing().`when`(financialRepository).deleteTransaction(any())
            `when`(financialRepository.get(uid = transaction.parent)).thenReturn(flowOf(parentTransaction))

            // Act
            val result = deleteTransactionUseCase(transaction, DeleteTransactionType.AllOccurrences)

            // Assert
            verify(financialRepository).deleteTransaction(transaction)
            verify(financialRepository).deleteTransaction(parentTransaction)
            assertTrue(result.isSuccess)
            assertEquals(true, result.getOrNull())
        }
}
