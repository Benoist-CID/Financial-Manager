package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use case that creates a [Transaction] from an incoming notification payload.
 *
 * Orchestrates notification validation, parsing, and persistence by delegating
 * to [NotificationHelper] and [CreateTransactionUseCase]. Returns `true` when
 * a transaction was successfully created, `false` for any non-fatal failure
 * (unrecognised notification, parse error).
 *
 * @property createTransactionUseCase Persists the parsed transaction.
 * @property notificationHelper Validates and parses the raw notification strings.
 * @property dispatcher Coroutine dispatcher for the blocking parse/persist work.
 *   Defaults to [Dispatchers.IO]; override in tests for determinism.
 * @property logger Domain [Logger] for diagnostic output. Defaults to [Logger.NoOp].
 */
class CreateTransactionFromNotificationUseCase(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val notificationHelper: NotificationHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val logger: Logger = Logger.NoOp,
) {
    suspend operator fun invoke(notificationTitle: String, notificationMessage: String): Boolean =
        withContext(dispatcher) {
            // 1. Guard Clause: Check if it is a transaction
            // .getOrDefault(false) is cleaner than comparing == Result.success(true)
            val isTransaction = notificationHelper.isTransaction(notificationMessage)
                .getOrDefault(false)

            if (!isTransaction) {
                logger.error("Not a valid transaction")
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
                logger.error(
                    message = "Failed to parse transaction",
                    throwable = transactionResult.exceptionOrNull()
                )
                return@withContext false
            }

            // 4. Happy Path: Create transaction and return success
            createTransactionUseCase(transaction)
            true
        }
}
