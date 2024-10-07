package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.interactors.DeleteTransactionInteractor
import fr.laforge.benoist.preferences.DataStorePreferencesInteractor
import fr.laforge.benoist.preferences.PreferencesInteractor
import org.koin.dsl.module

val interactorsModule by lazy {
    module {
        single<PreferencesInteractor> { DataStorePreferencesInteractor(context = get()) }
    }
}