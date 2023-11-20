package fr.laforge.benoist.financialmanager.di.module

import androidx.room.Room
import fr.laforge.benoist.repository.FinancialRepository
import fr.laforge.benoist.repository.implementations.room.AndroidFinancialRepository
import fr.laforge.benoist.repository.implementations.room.database.AppDatabase
import org.koin.dsl.module

val repositoryModule by lazy {
    module {
        single { Room.databaseBuilder(
                    get(),
                    AppDatabase::class.java, "database-name"
                ).build()
            }
        single<FinancialRepository> { AndroidFinancialRepository(get()) }
    }
}
