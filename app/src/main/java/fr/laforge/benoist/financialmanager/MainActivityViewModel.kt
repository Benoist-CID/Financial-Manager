package fr.laforge.benoist.financialmanager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.interactors.notification.CreateTransactionFromNotificationInteractor
import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.interactors.notification.EnableNotificationAccessUseCase
import fr.laforge.benoist.financialmanager.views.home.HomeScreenViewModel
import fr.laforge.benoist.model.Notification
import fr.laforge.benoist.util.getDateBoundaries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainActivityViewModel(
    private val createRegularTransactionsUseCase: CreateRegularTransactionsUseCase,
    private val enableNotificationAccessUseCase: EnableNotificationAccessUseCase,
    private val createTransactionFromNotificationInteractor: CreateTransactionFromNotificationInteractor,
) : ViewModel(), DefaultLifecycleObserver {
    private lateinit var navController: NavController

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Timber.d("onStart")
        navController.popBackStack(FinancialManagerScreen.Login.name, false)

        // Checks if notifications access is enabled, and enables them if not
        enableNotificationAccessUseCase()

        viewModelScope.launch(Dispatchers.IO) {
            createRegularTransactionsUseCase.execute(
                startDate = getDateBoundaries(startDay = HomeScreenViewModel.START_DAY).first.atTime(0, 0),
                endDate = getDateBoundaries(startDay = HomeScreenViewModel.START_DAY).second.atTime(0, 0)
            )
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }

    fun createTransaction(notification: Notification) {
        viewModelScope.launch {
            createTransactionFromNotificationInteractor(notification = notification)
        }
    }
}
