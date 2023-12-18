package fr.laforge.benoist.financialmanager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import timber.log.Timber

class MainActivityViewModel : ViewModel(), DefaultLifecycleObserver {
    var shouldDisplayLogin = false
    private lateinit var navController: NavController

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Timber.d("onStart")
        navController.popBackStack(FinancialManagerScreen.Login.name, false)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }
}
