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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.ui.addinput.AddInputScreen
import fr.laforge.benoist.financialmanager.ui.home.HomeScreen
import fr.laforge.benoist.financialmanager.ui.theme.FinancialManagerTheme

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
                            AddInputScreen(navController = navController)
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

enum class FinancialManagerScreen() {
    Home,
    AddInput
}
