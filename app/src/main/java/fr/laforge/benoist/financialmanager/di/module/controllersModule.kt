package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.controller.PreferencesController
import fr.laforge.benoist.financialmanager.controller.PreferencesControllerImpl
import org.koin.dsl.module

val controllersModule by lazy {
    module {
        single<PreferencesController> { PreferencesControllerImpl(preferencesInteractor = get()) }
    }
}
