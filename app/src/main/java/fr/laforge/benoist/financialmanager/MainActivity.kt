package fr.laforge.benoist.financialmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.laforge.benoist.financialmanager.views.transaction.add.AddTransactionScreen
import fr.laforge.benoist.financialmanager.views.home.HomeScreen
import fr.laforge.benoist.financialmanager.ui.theme.FinancialManagerTheme
import fr.laforge.benoist.financialmanager.views.db.ImportDbScreen
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetails
import fr.laforge.benoist.financialmanager.views.transaction.detail.TransactionDetailsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = FinancialManagerScreen.Home.name) {
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
                            val vm = TransactionDetailsViewModel(transactionId = backStackEntry.arguments?.getInt("transactionId")!!)
                            TransactionDetails(
                                navController = navController,
                                vm = vm
                            )
                        }

                        composable(FinancialManagerScreen.ImportDb.name) {
                            ImportDbScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FinancialManagerTheme {
        Greeting("Android")
    }
}

enum class FinancialManagerScreen {
    Home,
    AddInput,
    TransactionDetails,
    ImportDb
}
