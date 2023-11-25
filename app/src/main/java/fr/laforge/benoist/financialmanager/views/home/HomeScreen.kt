package fr.laforge.benoist.financialmanager.views.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.di.module.repositoryModule
import fr.laforge.benoist.financialmanager.ui.component.ShowDialog
import fr.laforge.benoist.financialmanager.ui.component.TransactionRow
import kotlinx.coroutines.launch
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
            SituationCard(amount = amount)

            LazyColumn {
                items(transactions) { transaction ->
                    val dismissState = rememberDismissState()
                    val openAlertDialog = remember { mutableStateOf(false) }

                    if (dismissState.isDismissed(direction = DismissDirection.EndToStart)) {
                        openAlertDialog.value = true
                    }

                    // Displays dialog

                    when {
                        openAlertDialog.value ->
                            ShowDialog(
                                onDismissRequest = {
                                    vm.viewModelScope.launch {
                                        dismissState.snapTo(DismissValue.Default)
                                    }
                                    openAlertDialog.value = false
                                },
                                onConfirmation = {
                                    vm.deleteTransaction(transaction = transaction)
                                    vm.viewModelScope.launch {
                                        dismissState.snapTo(DismissValue.Default)
                                    }
                                    openAlertDialog.value = false
                                },
                                dialogTitle = "Do you want to delete?",
                                dialogText = "Once deleted, it is not possible to undelete, think of it carefully.",
                                icon = {
                                    Icon(Icons.Filled.Delete, contentDescription = null)
                                }
                            )
                    }

                    Timber.d("dismissState: ${dismissState.currentValue}")

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.EndToStart
                        ),
                        background = {
                            // this background is visible when we swipe.
                            // it contains the icon

                            // background color
                            val backgroundColor by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.6f)
                                    else -> Color.White
                                }, label = ""
                            )

                            // icon size
                            val iconScale by animateFloatAsState(
                                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) 1.3f else 0.5f,
                                label = ""
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color = backgroundColor)
                                    .padding(end = 16.dp), // inner padding
                                contentAlignment = Alignment.CenterEnd // place the icon at the end (left)
                            ) {
                                Icon(
                                    modifier = Modifier.scale(iconScale),
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            // list item
                            TransactionRow(transaction) { transaction ->
                                Timber.i("Transaction #${transaction.uid}")
                                navController.navigate(FinancialManagerScreen.TransactionDetails.name)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {

}
