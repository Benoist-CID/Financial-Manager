package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.presentation.ui.MainActivityViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.db.ImportDbViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.home.HomeScreenViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.indicators.IndicatorsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.login.LoginViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.settings.SettingsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.add.AddTransactionViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring.RecurringManagementViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.update.UpdateTransactionViewModel
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
        viewModel { AddTransactionViewModel(createTransactionUseCase = get()) }

        viewModel {
            SettingsViewModel(
                preferencesRepository = get()
            )
        }

        viewModel {
            HomeScreenViewModel(
                repository = get(),
                transactionInteractor = get(),
                preferencesRepository = get(),
            )
        }

        viewModel {
            ImportDbViewModel(repository = get())
        }

        viewModel {
            LoginViewModel()
        }

        viewModel {
            TransactionDetailsViewModel(
                savedStateHandle = get(),
                financialRepository = get(),
            )
        }

        viewModel {
            UpdateTransactionViewModel(
                savedStateHandle = get(),
                financialRepository = get(),
                transactionInteractor = get(),
            )
        }

        viewModel {
            IndicatorsViewModel(
                getRecurringIncomeUseCase = get(),
                getRecurringExpensesUseCase = get(),
                getRegularExpensesUseCase = get(),
                getDailyBalanceUseCase = get(),
                getNonRecurringIncomeUseCase = get(),
            )
        }

        viewModel {
            RecurringManagementViewModel(
                getRecurringExpenseTemplatesUseCase = get(),
                deleteTransactionUseCase = get(),
            )
        }
    }
}
