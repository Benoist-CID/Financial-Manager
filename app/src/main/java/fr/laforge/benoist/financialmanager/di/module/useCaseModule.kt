package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCaseImpl
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.interactors.notification.CreateTransactionFromNotificationInteractor
import fr.laforge.benoist.financialmanager.interactors.notification.CreateTransactionFromNotificationInteractorImpl
import fr.laforge.benoist.financialmanager.interactors.notification.EnableNotificationAccessUseCase
import fr.laforge.benoist.financialmanager.interactors.notification.EnableNotificationAccessUseCaseImpl
import fr.laforge.benoist.financialmanager.usecase.notification.CreateTransactionNotificationUseCase
import fr.laforge.benoist.financialmanager.usecase.notification.CreateTransactionNotificationUseCaseImpl
import org.koin.dsl.module

val useCaseModule by lazy {
    module {
        factory<CreateTransactionUseCase> { CreateTransactionUseCaseImpl() }
        factory<CreateRegularTransactionsUseCase> { CreateRegularTransactionsUseCaseImpl() }
        factory<EnableNotificationAccessUseCase> { EnableNotificationAccessUseCaseImpl(context = get()) }
        factory<CreateTransactionFromNotificationInteractor> {
            CreateTransactionFromNotificationInteractorImpl(
                createTransactionUseCase = get(),
                notificationHelper = get()
            )
        }
        factory<CreateTransactionNotificationUseCase> {
            CreateTransactionNotificationUseCaseImpl(notificationHelper = get())
        }
    }
}
