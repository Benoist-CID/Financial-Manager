package fr.laforge.benoist.financialmanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.laforge.benoist.financialmanager.ui.theme.FinancialManagerTheme
import fr.laforge.benoist.financialmanager.views.db.ImportDbScreen
import fr.laforge.benoist.financialmanager.views.home.HomeScreen
import fr.laforge.benoist.financialmanager.views.login.LoginScreen
import fr.laforge.benoist.financialmanager.views.settings.SettingsScreen
import fr.laforge.benoist.financialmanager.views.transaction.add.AddTransactionScreen
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetails
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetailsViewModel
import fr.laforge.benoist.model.Notification
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {
    private val vm: MainActivityViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            intent?.let {
                val message = intent.extras?.getString("value")

                message?.let {
                    vm.createTransaction(notification = Notification(message = message))
                }
            }
        }

        setContent {
            lifecycle.addObserver(vm)

            FinancialManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    vm.setNavController(navController)
                    NavHost(navController = navController, startDestination = FinancialManagerScreen.Home.name) {
                        composable(FinancialManagerScreen.Login.name) {
                            LoginScreen(navController = navController)
                        }
                        composable(FinancialManagerScreen.Home.name) {
                            HomeScreen(navController = navController)
                        }

                        composable(FinancialManagerScreen.AddInput.name) {
                            AddTransactionScreen(navController = navController)
                        }

                        composable(
                            FinancialManagerScreen.TransactionDetails.name + "/{transactionId}",
                            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val vm = TransactionDetailsViewModel(
                                transactionId = backStackEntry.arguments?.getInt("transactionId")!!
                            )
                            TransactionDetails(
                                navController = navController,
                                vm = vm
                            )
                        }

                        composable(FinancialManagerScreen.ImportDb.name) {
                            ImportDbScreen(navController = navController)
                        }

                        composable(FinancialManagerScreen.Settings.name) {
                            SettingsScreen()
                        }
                    }

                }
            }
        }
    }
}

enum class FinancialManagerScreen {
    Login,
    Home,
    AddInput,
    TransactionDetails,
    ImportDb,
    Settings
}
