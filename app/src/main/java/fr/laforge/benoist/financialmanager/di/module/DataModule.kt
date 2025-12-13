package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.data.repository.PreferencesRepositoryImpl
import fr.laforge.benoist.financialmanager.data.source.SettingsDataSource
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.infrastructure.data.source.DataStoreSettingsDataSource
import org.koin.dsl.module

val dataModule by lazy {
    module {
        single<SettingsDataSource> { DataStoreSettingsDataSource(context = get()) }
        single<PreferencesRepository> { PreferencesRepositoryImpl(settingsDataSource = get()) }
    }
}