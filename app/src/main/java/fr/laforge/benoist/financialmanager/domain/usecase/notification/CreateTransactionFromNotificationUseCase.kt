package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRemainingBalanceUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class CreateTransactionFromNotificationUseCase(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val notificationHelper: NotificationHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(notificationTitle: String, notificationMessage: String): Boolean =
        withContext(dispatcher) {
            // 1. Guard Clause: Check if it is a transaction
            // .getOrDefault(false) is cleaner than comparing == Result.success(true)
            val isTransaction = notificationHelper.isTransaction(notificationMessage)
                .getOrDefault(false)

            if (!isTransaction) {
                Timber.e("Not a valid transaction")
                return@withContext false
            }

            // 2. Parse the message
            val transactionResult = notificationHelper.parseNotificationMessage(
                notificationTitle = notificationTitle,
                notificationMessage = notificationMessage
            )

            // 3. Guard Clause: Check if parsing succeeded
            val transaction = transactionResult.getOrNull()
            if (transaction == null) {
                // Log the actual error from the Result for debugging
                Timber.e(transactionResult.exceptionOrNull(), "Failed to parse transaction")
                return@withContext false
            }

            // 4. Happy Path: Create transaction and return success
            createTransactionUseCase(transaction)
            true
        }
}
