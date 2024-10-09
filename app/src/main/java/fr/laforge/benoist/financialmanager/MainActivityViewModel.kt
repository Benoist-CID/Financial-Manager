package fr.laforge.benoist.financialmanager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.ui.home.HomeScreenViewModel
import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.usecase.notification.EnableNotificationAccessUseCase
import fr.laforge.benoist.util.getDateBoundaries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivityViewModel(
    private val createRegularTransactionsUseCase: CreateRegularTransactionsUseCase,
    private val enableNotificationAccessUseCase: EnableNotificationAccessUseCase,
) : ViewModel(), DefaultLifecycleObserver {
    private lateinit var navController: NavController

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Timber.d("onStart")
        navController.popBackStack(FinancialManagerScreen.Login.name, false)

        // Checks if notifications access is anebled, and enables them if not
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
}
