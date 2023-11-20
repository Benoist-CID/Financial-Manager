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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.component.FinancialInputRow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: HomeScreenViewModel = HomeScreenViewModel()
) {
    val amount = vm.availableAmount.collectAsState()
    val transactions = vm.allTransactions.collectAsState()

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
                text = "${amount.value}€",
                fontSize = 30.sp
            )

            LazyColumn {
                items(transactions.value) { input ->
                    FinancialInputRow(input)
                }
            }
        }
    }
}
