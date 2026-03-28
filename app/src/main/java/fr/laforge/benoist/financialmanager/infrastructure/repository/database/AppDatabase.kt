package fr.laforge.benoist.financialmanager.infrastructure.repository.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.laforge.benoist.financialmanager.infrastructure.repository.converters.LocalDateTimeConverters
import fr.laforge.benoist.financialmanager.infrastructure.repository.converters.NotificationSourceConverters
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.FinancialInputDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.NotificationFormatDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.PendingTransactionDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.NotificationFormatEntity
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.PendingTransactionEntity
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.TransactionEntity

@Database(
    version = 4,
    entities = [
        TransactionEntity::class,
        PendingTransactionEntity::class,
        NotificationFormatEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true,
)
@TypeConverters(LocalDateTimeConverters::class, NotificationSourceConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFinancialInputDao(): FinancialInputDao
    abstract fun getPendingTransactionDao(): PendingTransactionDao
    abstract fun getNotificationFormatDao(): NotificationFormatDao

    companion object {
        /** Adds the pending_transaction table introduced in schema version 3. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `pending_transaction` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `amount` REAL NOT NULL,
                        `description` TEXT NOT NULL,
                        `detected_at` INTEGER NOT NULL,
                        `sources` TEXT NOT NULL,
                        `confidence` TEXT NOT NULL,
                        `status` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        /** Adds the notification_format table introduced in schema version 4. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `notification_format` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `description` TEXT NOT NULL,
                        `pattern` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
