package fr.laforge.benoist.financialmanager.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.presentation.ui.navigation.FinancialManagerNavHost
import fr.laforge.benoist.financialmanager.presentation.ui.theme.FinancialManagerTheme
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {
    private val vm: MainActivityViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                    FinancialManagerNavHost(navController = navController)
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
    UpdateTransaction,
    ImportDb,
    Settings,
    /** A screen continaing financial indicators **/
    Indicators,
    RecurringExpenses,
}
