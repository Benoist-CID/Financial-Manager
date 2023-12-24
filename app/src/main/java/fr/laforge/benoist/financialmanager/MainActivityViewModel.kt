package fr.laforge.benoist.financialmanager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.views.home.HomeScreenViewModel
import fr.laforge.benoist.util.getDateBoundaries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainActivityViewModel : ViewModel(), DefaultLifecycleObserver, KoinComponent {
    private lateinit var navController: NavController
    private val createRegularTransactionsUseCase: CreateRegularTransactionsUseCase by inject()

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Timber.d("onStart")
        navController.popBackStack(FinancialManagerScreen.Login.name, false)

        viewModelScope.launch(Dispatchers.IO) {
            createRegularTransactionsUseCase.execute(
                startDate = getDateBoundaries(startDay = HomeScreenViewModel.START_DAY).first.atTime(0, 0),
                endDate = getDateBoundaries(startDay = HomeScreenViewModel.START_DAY).second.atTime(0, 0)
            ).first()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }
}
