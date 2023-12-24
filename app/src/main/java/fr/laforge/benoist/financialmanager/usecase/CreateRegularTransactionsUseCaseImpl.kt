package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.util.isInRange
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime

class CreateRegularTransactionsUseCaseImpl : CreateRegularTransactionsUseCase, KoinComponent {
    private val repository: FinancialRepository by inject()

    override suspend  fun execute(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currentDate: LocalDateTime,
        type: TransactionType
    ): Flow<Boolean> = flow {
        val transactions = repository.getAllPeriodicTransactions().first()

        Timber.d("startDate: $startDate")
        Timber.d("endDate: $endDate")

        Timber.d("$transactions")

        transactions.forEach { transaction ->

            val childrenTransactions = repository.getChildrenTransactions(
                parentId = transaction.uid,
                startDate = startDate,
                endDate = endDate
            )

            if (childrenTransactions.isEmpty()) {
                var date = LocalDateTime.of(
                    currentDate.year,
                    currentDate.monthValue,
                    transaction.dateTime.dayOfMonth,
                    transaction.dateTime.hour,
                    transaction.dateTime.minute
                )

                if (!date.isInRange(startDate, endDate)) {
                    Timber.d("Task is not in date range")
                    var month = currentDate.monthValue
                    var year = currentDate.year
                    if (date > endDate) {
                        Timber.d("date > endDate")
                        Timber.d("date:$date")
                        Timber.d("endDate:$endDate")
                        month -= 1
                        if (month == JANUARY - 1) {
                            month = DECEMBER
                            year -= 1
                        }
                    } else {
                        Timber.d("date <= endDate")
                        month += 1
                        if (month == DECEMBER + 1) {
                            month = JANUARY
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

                Timber.d("Creating Transaction: ${
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

        emit(true)
    }

    companion object {
        const val JANUARY = 1
        const val DECEMBER = 12
    }
}
