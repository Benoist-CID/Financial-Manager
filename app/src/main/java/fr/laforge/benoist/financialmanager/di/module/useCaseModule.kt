package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCaseImpl
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.usecase.notification.CreateTransactionFromNotificationUseCase
import fr.laforge.benoist.financialmanager.usecase.notification.CreateTransactionFromNotificationUseCaseImpl
import fr.laforge.benoist.financialmanager.usecase.notification.EnableNotificationAccessUseCase
import fr.laforge.benoist.financialmanager.usecase.notification.EnableNotificationAccessUseCaseImpl
import org.koin.dsl.module

val useCaseModule by lazy {
    module {
        factory<CreateTransactionUseCase> { CreateTransactionUseCaseImpl() }
        factory<CreateRegularTransactionsUseCase> { CreateRegularTransactionsUseCaseImpl() }
        factory<EnableNotificationAccessUseCase> { EnableNotificationAccessUseCaseImpl(context = get()) }
        factory<CreateTransactionFromNotificationUseCase> {
            CreateTransactionFromNotificationUseCaseImpl(
                createTransactionUseCase = get(),
                notificationHelper = get()
            )
        }
    }
}
