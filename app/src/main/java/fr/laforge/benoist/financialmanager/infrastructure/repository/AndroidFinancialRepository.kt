package fr.laforge.benoist.financialmanager.infrastructure.repository

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.FinancialInputDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.fromModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class AndroidFinancialRepository(private val financialDao: FinancialInputDao) :
    FinancialRepository {
    override fun createTransaction(transaction: Transaction): Long {
        return financialDao.insertAll(listOf(fromModel(transaction = transaction)))[0]
    }

    override fun getTransactions(filter: TransactionFilter): Flow<List<Transaction>> =
        financialDao.getTransactions(
            type = filter.type?.name,
            startDate = filter.startDate,
            endDate = filter.endDate,
            search = filter.descriptionQuery,
            isPeriodic = filter.isPeriodic,
            parentId = filter.parentId?.toInt(),
        ).map {
            it.map { transactionEntity ->
                transactionEntity.toModel()
            }
        }

    override fun getAllInDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>> {
        return financialDao.getAllInDateRange(startDate, endDate).map {
            it.map { transactionEntity ->
                transactionEntity.toModel()
            }
        }
    }

    override fun getAllInDateRangeByDescription(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        description: String
    ): Flow<List<Transaction>> {
        return financialDao.getAllInDateRangeByDescription(startDate, endDate, description).map {
            it.map { transactionEntity ->
                transactionEntity.toModel()
            }
        }
    }

    override fun get(uid: Int): Flow<Transaction> =
        financialDao.getById(uid = uid).map { transactionEntity ->
            transactionEntity.toModel()
        }

    override fun getAllExpenses(): Flow<List<Transaction>> {
        return financialDao.getByInputType(inputType = TransactionType.Expense).map {
            it.map { input ->
                input.toModel()
            }
        }
    }

    override fun getAllIncomes(): Flow<List<Transaction>> {
        return financialDao.getByInputType(inputType = TransactionType.Income).map { entities ->
            entities.map {
                it.toModel()
            }
        }
    }

    override fun deleteTransaction(transaction: Transaction) {
        financialDao.delete(listOf(fromModel(transaction)))
    }

    override fun updateTransaction(transaction: Transaction) {
        financialDao.update(listOf(fromModel(transaction)))
    }

    override fun getAllPeriodicTransactions(): Flow<List<Transaction>> {
        return financialDao.getAllPeriodic().map { entities ->
            entities.map {
                it.toModel()
            }
        }
    }

    override fun getAllPeriodicTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return financialDao.getAllPeriodicByType(type).map { entities ->
            entities.map {
                it.toModel()
            }
        }
    }

    override fun getChildrenTransactions(
        parentId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return financialDao.getChildrenTransactions(
            parentId,
            startDate,
            endDate
        ).map { entities ->
            entities.toModel()
        }
    }

    override fun getBalanceBeforeDate(date: LocalDateTime): Flow<Float> =
        financialDao.getBalanceBefore(date)

    override fun getTransactionsBeforeDate(date: LocalDateTime): Flow<Transaction> =
        financialDao.getTransactionsBefore(date).map { it.toModel() }
}
