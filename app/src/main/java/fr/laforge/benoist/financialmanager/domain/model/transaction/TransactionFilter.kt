package fr.laforge.benoist.financialmanager.domain.model.transaction

import java.time.LocalDateTime

data class TransactionFilter(
    val type: TransactionType? = null, // null = All
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val descriptionQuery: String? = null,
    val isPeriodic: Boolean? = null,
    val parentId: Long? = null
) {
    companion object {
        /**
         * Creates a filter for monthly variable expenses.
         *
         * @param date The reference date to determine the month.
         */
        fun monthlyVariableExpenses(date: LocalDateTime): TransactionFilter {
            val startDate = date.withDayOfMonth(1).toLocalDate().atStartOfDay()
            val endDate = date.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()

            return TransactionFilter(
                type = TransactionType.Expense,
                startDate = startDate,
                endDate = endDate,
                descriptionQuery = "",
                isPeriodic = false,
                parentId = 0
            )
        }

        /**
         * Creates a filter for monthly recurring expenses.
         */
        fun monthlyRecurringExpenses() = TransactionFilter(
                type = TransactionType.Expense,
                startDate = null,
                endDate = null,
                descriptionQuery = "",
                isPeriodic = true,
                parentId = 0
            )

        /**
         * Creates a filter for monthly variable income.
         *
         * @param date The reference date to determine the month.
         */
        fun monthlyVariableIncome(date: LocalDateTime): TransactionFilter {
            val startDate = date.withDayOfMonth(1).toLocalDate().atStartOfDay()
            val endDate = date.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()

            return TransactionFilter(
                type = TransactionType.Income,
                startDate = startDate,
                endDate = endDate,
                descriptionQuery = "",
                isPeriodic = false,
                parentId = 0
            )
        }

        /**
         * Creates a filter for monthly recurring income.
         */
        fun monthlyRecurringIncome() = TransactionFilter(
            type = TransactionType.Income,
            startDate = null,
            endDate = null,
            descriptionQuery = "",
            isPeriodic = true,
            parentId = 0
        )
    }
}
