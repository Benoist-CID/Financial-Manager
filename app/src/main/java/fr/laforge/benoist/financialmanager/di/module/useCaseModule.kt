package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.domain.usecase.CheckIfTransactionIsPeriodicUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.CheckIfTransactionIsPeriodicUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.CreateRegularTransactionsUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractor
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractorImpl
import fr.laforge.benoist.financialmanager.domain.usecase.UpdateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.UpdateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.CalculateSituationProportionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetDailyBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRegularExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRemainingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.CreateTransactionFromNotificationUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.EnableNotificationAccessUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ExportTransactionsListUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetAllRecurringTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetAllTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringExpenseTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringIncomeTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringExpenseTemplatesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringIncomeTransactionsUseCase
import fr.laforge.benoist.financialmanager.infrastructure.usecase.EnableNotificationAccessUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule by lazy {
    module {
        factoryOf(::ExportTransactionsListUseCase)
        factoryOf(::GetAllTransactionsUseCase)
        factoryOf(::GetAllRecurringTransactionsUseCase)
        factory<CreateTransactionUseCase> { CreateTransactionUseCaseImpl() }
        factory<CreateRegularTransactionsUseCase> { CreateRegularTransactionsUseCaseImpl() }
        factory<EnableNotificationAccessUseCase> { EnableNotificationAccessUseCaseImpl(context = get()) }
        factory {
            CreateTransactionFromNotificationUseCase(
                createTransactionUseCase = get(),
                notificationHelper = get(),
            )
        }

        factory<CheckIfTransactionIsPeriodicUseCase> {
            CheckIfTransactionIsPeriodicUseCaseImpl()
        }
        factory<DeleteTransactionUseCase> {
            DeleteTransactionUseCaseImpl(
                financialRepository = get()
            )
        }
        factory<UpdateTransactionUseCase> {
            UpdateTransactionUseCaseImpl(
                financialRepository = get()
            )
        }
        factory<TransactionInteractor> {
            TransactionInteractorImpl(
                checkIfTransactionIsPeriodicUseCase = get(),
                deleteTransactionUseCase = get(),
                updateTransactionUseCase = get(),
            )
        }

        factory {
            GetRecurringIncomeUseCase(
                financialRepository = get()
            )
        }

        factory {
            GetRecurringExpensesUseCase(
                financialRepository = get()
            )
        }

        factory {
            GetRegularExpensesUseCase(
                financialRepository = get()
            )
        }

        factory {
            GetRecurringExpenseTemplatesUseCase(
                repository = get()
            )
        }

        factory {
            GetRecurringIncomeTransactionsUseCase(
                repository = get()
            )
        }

        factoryOf(::GetDailyBalanceUseCase)

        factory {
            GetNonRecurringIncomeUseCase(
                financialRepository = get()
            )
        }

        factory {
            GetRemainingBalanceUseCase(
                repository = get()
            )
        }

        factory {
            GetNonRecurringExpenseTransactionsUseCase(
                repository = get()
            )
        }

        factory {
            GetNonRecurringIncomeTransactionsUseCase(
                repository = get()
            )
        }

        factoryOf(::GetMonthStartingBalanceUseCase)
        factoryOf(::GetMonthlyTransactionsUseCase)
        factoryOf(::CalculateSituationProportionsUseCase)
    }
}
