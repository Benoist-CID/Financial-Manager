package fr.laforge.benoist.repository

import fr.laforge.benoist.model.Transaction
import kotlinx.coroutines.flow.Flow

interface FinancialRepository {
    /**
     * Creates a new FinancialInput
     */
    fun createTransaction(transaction: Transaction)

    /**
     * Returns all FinancialInput
     */
    fun getAll(): Flow<List<Transaction>>

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
}
