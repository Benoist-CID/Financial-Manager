package fr.laforge.benoist.financialmanager.infrastructure

import android.app.Application
import fr.laforge.benoist.financialmanager.data.AppContainer
import fr.laforge.benoist.financialmanager.data.AppDataContainer
import fr.laforge.benoist.financialmanager.di.module.dataModule
import fr.laforge.benoist.financialmanager.di.module.helperModule
import fr.laforge.benoist.financialmanager.di.module.repositoryModule
import fr.laforge.benoist.financialmanager.di.module.useCaseModule
import fr.laforge.benoist.financialmanager.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class FinancialManagerApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare used Android context
            androidContext(this@FinancialManagerApp)
            modules(viewModelModule)
            modules(repositoryModule)
            modules(useCaseModule)
            modules(helperModule)
            modules(dataModule)
        }

        Timber.Forest.plant(Timber.DebugTree())

        container = AppDataContainer(this)
    }
}