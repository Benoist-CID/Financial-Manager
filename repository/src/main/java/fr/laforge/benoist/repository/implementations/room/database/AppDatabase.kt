package fr.laforge.benoist.repository.implementations.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.laforge.benoist.repository.implementations.room.converters.LocalDateTimeConverters
import fr.laforge.benoist.repository.implementations.room.dao.FinancialInputDao
import fr.laforge.benoist.repository.implementations.room.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1)
@TypeConverters(LocalDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFinancialInputDao(): FinancialInputDao
}
