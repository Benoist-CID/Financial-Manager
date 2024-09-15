package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.MainActivityViewModel
import fr.laforge.benoist.financialmanager.views.home.HomeScreenViewModel
import fr.laforge.benoist.financialmanager.views.transaction.add.AddTransactionViewModel
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetails
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule by lazy {
    module {
        viewModel {
            MainActivityViewModel(
                createRegularTransactionsUseCase = get(),
                enableNotificationAccessUseCase = get(),
                createTransactionFromNotificationInteractor = get(),
            )
        }
        viewModel { AddTransactionViewModel() }
        viewModel  {
            HomeScreenViewModel(
                preferencesController = get(),
                repository = get(),
            )
        }
    }
}
