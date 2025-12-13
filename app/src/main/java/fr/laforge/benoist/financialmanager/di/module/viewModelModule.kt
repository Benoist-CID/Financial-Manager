package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.presentation.ui.MainActivityViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.home.HomeScreenViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.settings.SettingsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.add.AddTransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule by lazy {
    module {
        viewModel {
            MainActivityViewModel(
                createRegularTransactionsUseCase = get(),
                enableNotificationAccessUseCase = get(),
            )
        }
        viewModel { AddTransactionViewModel() }

        viewModel {
            SettingsViewModel(
                preferencesRepository = get()
            )
        }

        viewModel {
            HomeScreenViewModel(
                repository = get(),
                transactionInteractor = get(),
                preferencesRepository = get()
            )
        }
    }
}
