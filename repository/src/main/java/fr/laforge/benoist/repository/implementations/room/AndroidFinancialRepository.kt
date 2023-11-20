package fr.laforge.benoist.repository.implementations.room

import fr.laforge.benoist.repository.FinancialRepository
import fr.laforge.benoist.repository.implementations.room.database.AppDatabase
import fr.laforge.benoist.repository.implementations.room.entity.fromModel
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AndroidFinancialRepository(database: AppDatabase) : FinancialRepository {
    private val financialDao = database.getFinancialInputDao()

    override fun createFinancialInput(transaction: Transaction) {
        financialDao.insertAll(fromModel(transaction = transaction))
    }

    override fun getAll(): Flow<List<Transaction>> {
        return financialDao.getAll().map {
            it.map { input ->
                input.toModel()
            }
        }
    }

    override fun getAllExpenses(): Flow<List<Transaction>> {
        return financialDao.getByInputType(inputType = InputType.Expense).map {
            it.map { input ->
                input.toModel()
            }
        }
    }

    override fun getAllIncomes(): Flow<List<Transaction>> {
        return financialDao.getByInputType(inputType = InputType.Income).map { entities ->
            entities.map {
                it.toModel()
            }
        }
    }
}
