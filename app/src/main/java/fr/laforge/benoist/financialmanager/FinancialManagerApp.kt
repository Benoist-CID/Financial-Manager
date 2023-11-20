package fr.laforge.benoist.financialmanager

import android.app.Application
import fr.laforge.benoist.financialmanager.di.module.repositoryModule
import fr.laforge.benoist.financialmanager.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class FinancialManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // declare used Android context
            androidContext(this@FinancialManagerApp)
            modules(viewModelModule)
            modules(repositoryModule)
        }

        Timber.plant(Timber.DebugTree())
    }
}
