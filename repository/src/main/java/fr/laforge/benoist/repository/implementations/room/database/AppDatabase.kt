package fr.laforge.benoist.repository.implementations.room.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.repository.implementations.room.converters.LocalDateTimeConverters
import fr.laforge.benoist.repository.implementations.room.dao.FinancialInputDao
import fr.laforge.benoist.repository.implementations.room.entity.TransactionEntity

@Database(
    version = 2,
    entities = [TransactionEntity::class],
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFinancialInputDao(): FinancialInputDao
}
