package fr.laforge.benoist.financialmanager.interactors.notification

import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCase
import fr.laforge.benoist.model.Notification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

interface CreateTransactionFromNotificationInteractor {
    suspend operator fun invoke(notification: Notification): Any
}

class CreateTransactionFromNotificationInteractorImpl(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val notificationHelper: NotificationHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CreateTransactionFromNotificationInteractor {
    override suspend fun invoke(notification: Notification) =
        withContext(dispatcher) {
            // Checks if the message contains a transaction
            if (notificationHelper.isTransaction(notification = notification) == Result.success(
                    true
                )
            ) {
                // Creates a transaction from message
                val transaction =
                    notificationHelper.parseNotificationMessage(notificationMessage = notification.message)
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
