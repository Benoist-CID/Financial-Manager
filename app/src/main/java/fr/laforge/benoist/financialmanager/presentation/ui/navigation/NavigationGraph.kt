package fr.laforge.benoist.financialmanager.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.db.ImportDbScreen
import fr.laforge.benoist.financialmanager.presentation.ui.home.HomeScreen
import fr.laforge.benoist.financialmanager.presentation.ui.indicators.IndicatorsScreen
import fr.laforge.benoist.financialmanager.presentation.ui.login.LoginScreen
import fr.laforge.benoist.financialmanager.presentation.ui.settings.SettingsScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.add.AddTransactionScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail.TransactionDetails
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring.NonRecurringExpensesScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring.NonRecurringIncomeScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring.RecurringExpensesScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring.RecurringIncomesScreen
import fr.laforge.benoist.financialmanager.presentation.ui.pending.PendingTransactionsScreen
import fr.laforge.benoist.financialmanager.presentation.ui.settings.notificationformat.NotificationFormatScreen
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.update.UpdateTransaction

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

        composable(
            route = FinancialManagerScreen.UpdateTransaction.name + "/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType }),
        ) {
            UpdateTransaction(navController = navController)
        }

        composable(FinancialManagerScreen.ImportDb.name) {
            ImportDbScreen(navController = navController)
        }

        composable(FinancialManagerScreen.Settings.name) {
            SettingsScreen(navController = navController)
        }

        composable(FinancialManagerScreen.Indicators.name) {
            IndicatorsScreen(navController = navController)
        }

        composable(FinancialManagerScreen.RecurringExpenses.name) {
            RecurringExpensesScreen(navController = navController)
        }

        composable(FinancialManagerScreen.RecurringIncome.name) {
            RecurringIncomesScreen(navController = navController)
        }

        composable(FinancialManagerScreen.NonRecurringExpenses.name) {
            NonRecurringExpensesScreen(navController = navController)
        }

        composable(FinancialManagerScreen.NonRecurringIncome.name) {
            NonRecurringIncomeScreen(navController = navController)
        }

        composable(FinancialManagerScreen.PendingTransactions.name) {
            PendingTransactionsScreen(navController = navController)
        }

        composable(FinancialManagerScreen.NotificationFormats.name) {
            NotificationFormatScreen(navController = navController)
        }
    }
}
