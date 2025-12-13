package fr.laforge.benoist.financialmanager.di.module

import androidx.room.Room
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.AndroidFinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.database.AppDatabase
import org.koin.dsl.module

val repositoryModule by lazy {
    module {
        single { Room.databaseBuilder(
                get(),
                AppDatabase::class.java, "database-name"
            ).build().getFinancialInputDao()
        }
        single<FinancialRepository> { AndroidFinancialRepository(get()) }
    }
}
