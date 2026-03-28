package fr.laforge.benoist.financialmanager.di.module

import androidx.room.Room
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.AndroidFinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.RoomNotificationFormatRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.RoomPendingTransactionRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.database.AppDatabase
import org.koin.dsl.module

val repositoryModule by lazy {
    module {
        // Single AppDatabase instance shared by all DAOs. MIGRATION_2_3 is registered
        // manually because AutoMigration is incompatible with Room KSP 2.3.4 for this schema.
        single {
            Room.databaseBuilder(
                get(),
                AppDatabase::class.java,
                "database-name",
            )
                .addMigrations(AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4, AppDatabase.MIGRATION_4_5)
                .build()
        }

        single { get<AppDatabase>().getFinancialInputDao() }
        single { get<AppDatabase>().getPendingTransactionDao() }
        single { get<AppDatabase>().getNotificationFormatDao() }

        single<FinancialRepository> { AndroidFinancialRepository(get()) }
        single<PendingTransactionRepository> { RoomPendingTransactionRepository(dao = get()) }
        single<NotificationFormatRepository> { RoomNotificationFormatRepository(dao = get()) }
    }
}
