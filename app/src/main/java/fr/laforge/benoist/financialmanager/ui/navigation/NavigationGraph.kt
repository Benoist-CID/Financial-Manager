package fr.laforge.benoist.financialmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.views.db.ImportDbScreen
import fr.laforge.benoist.financialmanager.views.home.HomeScreen
import fr.laforge.benoist.financialmanager.views.login.LoginScreen
import fr.laforge.benoist.financialmanager.views.settings.SettingsScreen
import fr.laforge.benoist.financialmanager.views.transaction.add.AddTransactionScreen
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetails
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetailsViewModel

@Composable
fun FinancialManagerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = FinancialManagerScreen.Home.name,
        modifier = modifier
    ) {
        composable(route = FinancialManagerScreen.Login.name) {
            LoginScreen(navController = navController)
        }
        composable(route = FinancialManagerScreen.Home.name) {
            HomeScreen(navController = navController)
        }

        composable(route = FinancialManagerScreen.AddInput.name) {
            AddTransactionScreen(navController = navController)
        }

        composable(
            route = FinancialManagerScreen.TransactionDetails.name + "/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) {
            TransactionDetails(navController = navController)
        }

        composable(FinancialManagerScreen.ImportDb.name) {
            ImportDbScreen(navController = navController)
        }

        composable(FinancialManagerScreen.Settings.name) {
            SettingsScreen()
        }
    }
}
