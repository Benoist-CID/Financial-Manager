package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.util.isInRange
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import java.time.Month
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.util.Logger
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

/**
 * Default implementation of [CreateRegularTransactionsUseCase].
 *
 * For each periodic transaction template, generates the corresponding child instance
 * within the given date window if one does not already exist.
 *
 * @property repository The [FinancialRepository] used to read templates and persist
 *   generated child transactions.
 * @property logger Domain [Logger] for diagnostic output. Defaults to [Logger.NoOp]
 *   so callers (including tests) are never forced to supply one.
 */
class CreateRegularTransactionsUseCaseImpl(
    private val repository: FinancialRepository,
    private val logger: Logger = Logger.NoOp
) : CreateRegularTransactionsUseCase {

    override suspend fun execute(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currentDate: LocalDateTime,
        type: TransactionType
    ): Boolean {
        val transactions = repository.getAllPeriodicTransactions().first()

        logger.debug("startDate: $startDate")
        logger.debug("endDate: $endDate")
        logger.debug("$transactions")

        transactions.forEach { transaction ->

            val childrenTransactions = repository.getChildrenTransactions(
                parentId = transaction.uid,
                startDate = startDate,
                endDate = endDate
            )

            logger.debug("Children transactions: $childrenTransactions")

            if (childrenTransactions.isEmpty()) {
                logger.debug("No children transactions, let's create them")
                var date = LocalDateTime.of(
                    currentDate.year,
                    currentDate.monthValue,
                    transaction.dateTime.dayOfMonth,
                    transaction.dateTime.hour,
                    transaction.dateTime.minute
                )

                if (!date.isInRange(startDate, endDate)) {
                    logger.debug("Task is not in date range")
                    var month = currentDate.monthValue
                    var year = currentDate.year
                    if (date > endDate) {
                        logger.debug("date > endDate — date:$date endDate:$endDate")
                        month -= 1
                        if (month == Month.JANUARY.value - 1) {
                            month = Month.DECEMBER.value
                            year -= 1
                        }
                    } else {
                        logger.debug("date <= endDate")
                        month += 1
                        if (month == Month.DECEMBER.value + 1) {
                            month = Month.JANUARY.value
                            year += 1
                        }
                    }

                    date = LocalDateTime.of(
                        year,
                        month,
                        transaction.dateTime.dayOfMonth,
                        transaction.dateTime.hour,
                        transaction.dateTime.minute
                    )
                }

                logger.debug("Creating Transaction: ${
                    transaction.copy(
                        uid = 0,
                        isPeriodic = false,
                        period = TransactionPeriod.None,
                        dateTime = date,
                        parent = transaction.uid
                    )
                }")

                repository.createTransaction(
                    transaction.copy(
                        uid = 0,
                        isPeriodic = false,
                        period = TransactionPeriod.None,
                        dateTime = date,
                        parent = transaction.uid
                    )
                )
            }
        }

        return true
    }

}
