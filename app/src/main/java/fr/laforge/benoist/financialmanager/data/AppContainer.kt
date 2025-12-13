package fr.laforge.benoist.financialmanager.data

import android.content.Context
import androidx.room.Room
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.AndroidFinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.database.AppDatabase

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val financialRepository: FinancialRepository
}

/**
 * [AppContainer] implementation that provides instance of [AndroidFinancialRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [FinancialRepository]
     */
    override val financialRepository: FinancialRepository by lazy {
        AndroidFinancialRepository(
            Room.databaseBuilder(
                this.context,
                AppDatabase::class.java, "database-name"
            ).build().getFinancialInputDao()
        )
    }
}
