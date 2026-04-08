package fr.laforge.benoist.financialmanager.infrastructure.repository.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.laforge.benoist.financialmanager.infrastructure.repository.converters.LocalDateTimeConverters
import fr.laforge.benoist.financialmanager.infrastructure.repository.converters.NotificationSourceConverters
import fr.laforge.benoist.financialmanager.infrastructure.repository.converters.SyncStatusConverter
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.FinancialInputDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.NotificationFormatDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.PendingTransactionDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.NotificationFormatEntity
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.PendingTransactionEntity
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.TransactionEntity

@Database(
    version = 7,
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
@TypeConverters(LocalDateTimeConverters::class, NotificationSourceConverters::class, SyncStatusConverter::class)
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

        /**
         * Adds the `sync_status` column to `TransactionEntity` introduced in schema version 5.
         *
         * All existing rows default to `'PENDING'` — they have never been reconciled against
         * a bank statement and must go through the sync process to reach [IN_SYNC][fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus.IN_SYNC].
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `TransactionEntity` ADD COLUMN `sync_status` TEXT NOT NULL DEFAULT 'PENDING'"
                )
            }
        }

        /**
         * Defensive migration from version 5 to 6.
         *
         * Ensures `sync_status` is present on `TransactionEntity`. Normally this column is
         * added by [MIGRATION_4_5], but devices that were upgraded to v5 during a development
         * build where [MIGRATION_4_5] was not yet registered in `.addMigrations()` may have
         * reached v5 without it. The `PRAGMA table_info` check makes this migration safe to
         * run in both cases (column absent → add it; column present → skip).
         *
         * This migration also reconciles the Room identity-hash that changed when
         * [SyncStatusConverter] was added to `@TypeConverters` after some v5 builds had
         * already been deployed.
         */
        /**
         * Recreates the `notification_format` table to remove the `description_source` column
         * that was added in an intermediate development build and later dropped from the entity.
         *
         * SQLite does not support `DROP COLUMN` on Android versions below API 34 (SQLite 3.35),
         * so the standard rename-copy-drop approach is used:
         * 1. Rename the old table to a temporary name.
         * 2. Create the canonical table with only the columns the entity expects.
         * 3. Copy the surviving columns from the old table.
         * 4. Drop the old table.
         *
         * @note Only `id`, `description`, and `pattern` are kept; `description_source` data
         *   is intentionally discarded as the column is no longer part of the domain model.
         */
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `notification_format` RENAME TO `notification_format_old`")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `notification_format` (
                        `id` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `pattern` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `notification_format` (`id`, `description`, `pattern`)
                    SELECT `id`, `description`, `pattern` FROM `notification_format_old`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `notification_format_old`")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("PRAGMA table_info(TransactionEntity)")
                val nameIndex = cursor.getColumnIndex("name")
                var hasSyncStatus = false
                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex) == "sync_status") {
                        hasSyncStatus = true
                        break
                    }
                }
                cursor.close()

                if (!hasSyncStatus) {
                    db.execSQL(
                        "ALTER TABLE `TransactionEntity` ADD COLUMN `sync_status` TEXT NOT NULL DEFAULT 'PENDING'"
                    )
                }
            }
        }
    }
}
