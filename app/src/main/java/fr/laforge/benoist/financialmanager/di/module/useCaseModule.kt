package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCaseImpl
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCase
import fr.laforge.benoist.financialmanager.usecase.CreateTransactionUseCaseImpl
import org.koin.dsl.module

val useCaseModule by lazy {
    module {
        factory<CreateTransactionUseCase> { CreateTransactionUseCaseImpl() }
        factory<CreateRegularTransactionsUseCase> { CreateRegularTransactionsUseCaseImpl() }
    }
}
