package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.presentation.ui.MainActivityViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.db.ImportDbViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.home.HomeScreenViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.home.situation.card.SituationCardViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.indicators.IndicatorsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.login.LoginViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.settings.SettingsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.add.AddTransactionViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring.NonRecurringManagementViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring.RecurringManagementViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.pending.PendingTransactionsViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.settings.notificationformat.NotificationFormatViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.home.upcoming.UpcomingExpensesViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.sync.SyncReviewViewModel
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.update.UpdateTransactionViewModel
import androidx.fragment.app.FragmentActivity
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetTransactionByIdUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ImportTransactionsUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
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

        viewModelOf(::SettingsViewModel)

        viewModelOf(::HomeScreenViewModel)

        viewModel {
            ImportDbViewModel(importTransactionsUseCase = get())
        }

        viewModel { (activity: FragmentActivity) ->
            LoginViewModel(
                biometricAuthenticator = get { parametersOf(activity) },
            )
        }

        viewModel {
            TransactionDetailsViewModel(
                savedStateHandle = get(),
                getTransactionByIdUseCase = get(),
            )
        }

        viewModel {
            UpdateTransactionViewModel(
                savedStateHandle = get(),
                getTransactionByIdUseCase = get(),
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
                getMonthStartingBalanceUseCase = get(),
            )
        }

        viewModel {
            RecurringManagementViewModel(
                getRecurringExpenseTemplatesUseCase = get(),
                deleteTransactionUseCase = get(),
                getRecurringIncomeTransactionsUseCase = get(),
            )
        }

        viewModel {
            NonRecurringManagementViewModel(
                getNonRecurringExpenseTransactionsUseCase = get(),
                deleteTransactionUseCase = get(),
                getNonRecurringIncomeTransactionsUseCase = get(),
            )
        }
        
        viewModelOf(::SituationCardViewModel)
        viewModelOf(::UpcomingExpensesViewModel)

        viewModel {
            PendingTransactionsViewModel(
                repository = get(),
                confirmPendingTransactionUseCase = get(),
                dismissPendingTransactionUseCase = get(),
            )
        }

        viewModel {
            NotificationFormatViewModel(
                getAllNotificationFormatsUseCase = get(),
                addNotificationFormatUseCase = get(),
                deleteNotificationFormatUseCase = get(),
            )
        }

        viewModel {
            SyncReviewViewModel(
                runSyncUseCase = get(),
                applySyncMatchUseCase = get(),
                createTransactionFromBankUseCase = get(),
                parseCsvBankTransactionsUseCase = get(),
            )
        }
    }
}
