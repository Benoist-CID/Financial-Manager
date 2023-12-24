package fr.laforge.benoist.repository.implementations.room

import fr.laforge.benoist.repository.FinancialRepository
import fr.laforge.benoist.repository.implementations.room.database.AppDatabase
import fr.laforge.benoist.repository.implementations.room.entity.fromModel
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class AndroidFinancialRepository(database: AppDatabase) : FinancialRepository {
    private val financialDao = database.getFinancialInputDao()

    override fun createTransaction(transaction: Transaction): Long {
        return financialDao.insertAll(fromModel(transaction = transaction))[0]
    }

    override fun getAll(): Flow<List<Transaction>> {
        return financialDao.getAll().map {
            it.map { input ->
                input.toModel()
            }
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

    override fun get(uid: Int): Flow<Transaction> {
        return financialDao.getById(uid = uid).map {
                transactionEntity -> transactionEntity.toModel()
            }
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
        financialDao.delete(fromModel(transaction))
    }

    override fun getAllPeriodicTransactions(): Flow<List<Transaction>> {
        return financialDao.getAllPeriodic().map { entities ->
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
}
