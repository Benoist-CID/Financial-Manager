package fr.laforge.benoist.repository.implementations.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.laforge.benoist.model.InputType
import fr.laforge.benoist.repository.implementations.room.entity.FinancialInputEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialInputDao {
    @Insert
    fun insertAll(vararg financialInputs: FinancialInputEntity)

    @Query("SELECT * FROM financialinputentity")
    fun getAll(): Flow<List<FinancialInputEntity>>

    @Query("SELECT * FROM FinancialInputEntity WHERE type=:inputType")
    fun getByInputType(inputType: InputType): Flow<List<FinancialInputEntity>>
}
