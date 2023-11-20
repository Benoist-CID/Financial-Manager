package fr.laforge.benoist.financialmanager.ui.addinput

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.AppViewModelProvider
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.model.InputType

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInputScreen(
    vm: AddInputViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    vm.getAllInputs()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.create_input)) },
                icon = {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Favorite",
                    )
                },
                onClick = {
                    vm.createFinancialInput()
                    navController.navigate(FinancialManagerScreen.Home.name)
                }
            )
        },
    ) {
        var checked by remember { mutableStateOf(true) }
        var expenseOrIncome by remember {
            mutableStateOf("Expense")
        }
        Column {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Add input",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding() + 16.dp,
                        start = it.calculateStartPadding(LayoutDirection.Ltr) + 16.dp
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding() + 16.dp,
                        end = it.calculateStartPadding(LayoutDirection.Ltr) + 16.dp
                    )
                ) {
                    Text(
                        text = expenseOrIncome,
                        modifier.padding(16.dp)
                    )
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            expenseOrIncome = when (it) {
                                true -> {
                                    "Expense"
                                }
                                false -> {
                                    "Income"
                                }
                            }

                            vm.updateInputType(it)
                        }
                    )
                }
            }

            OutlinedTextField(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = vm.amount.toString(),
                onValueChange = { newVal -> vm.updateAmount(newVal) },
                label = {
                    Text(
                        text = stringResource(id = R.string.amount),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = vm.description,
                onValueChange = { newDescription -> vm.updateDescription(newDescription) },
                label = {
                    Text(
                        text = stringResource(id = R.string.description),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
            )
        }
    }
}
