package fr.laforge.benoist.financialmanager.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.component.TransactionRow
import timber.log.Timber

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: HomeScreenViewModel = HomeScreenViewModel()
) {
    val amount by vm.availableAmount.collectAsState(0.0F)
    val transactions by vm.allTransactions.collectAsState(emptyList())

    Timber.i("HomeScreen drawn")

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.add_input)) },
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Favorite",
                    )
                },
                onClick = { navController.navigate(FinancialManagerScreen.AddInput.name) }
            )
        },
    ) {

        Column {
            Text(
                text = "${amount}€",
                fontSize = 30.sp
            )

            LazyColumn {
                items(transactions) { input ->
                    TransactionRow(input) {
                        navController.navigate(FinancialManagerScreen.TransactionDetails.name)
                    }
                }
            }
        }
    }
}
