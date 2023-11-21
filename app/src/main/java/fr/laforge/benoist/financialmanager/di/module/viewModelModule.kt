package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.views.addinput.AddTransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule by lazy {
    module {
        viewModel { AddTransactionViewModel() }
    }
}
