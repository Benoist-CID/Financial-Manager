package fr.laforge.benoist.financialmanager.domain.repository

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface FinancialRepository {
    /**
     * Creates a new FinancialInput
     */
    fun createTransaction(transaction: Transaction): Long

    /**
     * The single source of truth for lists.
     * The implementation (Room) will apply the filters in the WHERE clause.
     */
    fun getTransactions(filter: TransactionFilter): Flow<List<Transaction>>

    /**
     * Returns all FinancialInput
     */
    @Deprecated("Use getTransactions(filter: TransactionFilter) instead")
    fun getAll(): Flow<List<Transaction>>

    /**
     * Returns all Transaction ins specified date range
     */
    fun getAllInDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>

    /**
     * Returns all Transaction ins specified date range
     */
    fun getAllInDateRangeByDescription(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        description: String
    ): Flow<List<Transaction>>

    /**
     * Gets a Transaction by its unique ID
     */
    fun get(uid: Int): Flow<Transaction>

    /**
     * Returns all FinancialInput with type FinancialType.Expense
     */
    fun getAllExpenses(): Flow<List<Transaction>>

    /**
     * Returns all FinancialInput with type FinancialType.Income
     */
    fun getAllIncomes(): Flow<List<Transaction>>

    /**
     * Deletes the transaction from the repository
     *
     * @param transaction Transaction to delete
     */
    fun deleteTransaction(transaction: Transaction)

    /**
     * Updates the transaction
     *
     * @param transaction Transaction to update
     */
    fun updateTransaction(transaction: Transaction)

    /**
     * Returns all periodic transactions
     */
    fun getAllPeriodicTransactions(): Flow<List<Transaction>>

    /**
     * Returns all periodic transactions by type
     */
    fun getAllPeriodicTransactionsByType(type: TransactionType = TransactionType.Expense): Flow<List<Transaction>>

    /**
     * Returns all children transactions for specified parent ID in the date range
     *
     * @param parentId Parent ID
     * @param startDate First date in range
     * @param endDate Last date in range
     */
    fun getChildrenTransactions(
        parentId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    /**
    * Returns the sum of all transaction amounts strictly before the given date.
    * This represents the "Balance History" up to that point.
    *
    * @param date Date to get the balance before
    *
    * @return Flow of the balance
    */
    fun getBalanceBeforeDate(date: LocalDateTime = LocalDateTime.now()): Flow<Float>

    fun getTransactionsBeforeDate(date: LocalDateTime = LocalDateTime.now()): Flow<Transaction>
}
