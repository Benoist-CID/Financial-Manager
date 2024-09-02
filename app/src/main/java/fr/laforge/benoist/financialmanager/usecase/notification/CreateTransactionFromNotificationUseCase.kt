package fr.laforge.benoist.financialmanager.usecase.notification

import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

interface CreateTransactionFromNotificationUseCase {
    suspend operator fun invoke(notificationMessage: String): Any
}

class CreateTransactionFromNotificationUseCaseImpl(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val notificationHelper: NotificationHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CreateTransactionFromNotificationUseCase {
    override suspend fun invoke(notificationMessage: String) = withContext(dispatcher) {
        // Checks if the message contains a transaction
        if (notificationHelper.isTransaction(notificationMessage = notificationMessage) == Result.success(true)) {
            // Creates a transaction from message
            val transaction = notificationHelper.parseNotificationMessage(notificationMessage = notificationMessage)
            // Creates transaction
            transaction.getOrNull()?.let {
                createTransactionUseCase(transaction = it)
            } ?: {
                // Handle error
            }
        } else {
            Timber.e("Not a valid transaction")
        }
    }
}
