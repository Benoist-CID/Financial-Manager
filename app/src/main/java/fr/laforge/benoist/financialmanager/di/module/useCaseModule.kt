package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.domain.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.CreateRegularTransactionsUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.notification.CreateTransactionFromNotificationUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.CreateTransactionFromNotificationUseCaseImpl
import fr.laforge.benoist.financialmanager.domain.usecase.notification.EnableNotificationAccessUseCase
import fr.laforge.benoist.financialmanager.infrastructure.usecase.EnableNotificationAccessUseCaseImpl
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
