package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.MainActivityViewModel
import fr.laforge.benoist.financialmanager.ui.transaction.add.AddTransactionViewModel
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
    }
}
