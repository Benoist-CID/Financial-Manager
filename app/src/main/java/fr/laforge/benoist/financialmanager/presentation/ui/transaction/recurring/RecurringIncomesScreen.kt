package fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.SearchComponent
import fr.laforge.benoist.financialmanager.presentation.ui.component.SwipableTransactionItem
import fr.laforge.benoist.financialmanager.presentation.ui.component.formatAmount
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringIncomesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: RecurringManagementViewModel = koinViewModel(),
) {
    // Collect Data
    val transactions by vm.recurringIncomesItems.collectAsState()
    val totalMonthly by vm.totalIncomesMonthly.collectAsState()
    val query by vm.query.collectAsState()

    Scaffold(
        topBar = {
            // Using a standard TopAppBar styled to match your Home Screen Green
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Recurring Income",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00C853), // Your App Green
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                )
        ) {
            // --- HEADER AREA (Background matched to HomeScreen) ---
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                // 1. Small Total Summary (Replaces SituationCard)
                // We need this to see the total of fixed bills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total / Month",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "${formatAmount(totalMonthly)} €",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 2. Search Component (Exact same as Home)
                SearchComponent(
                    query = query,
                    onQueryChange = { newVal -> vm.updateSearch(newVal) }
                )
            }

            // --- LIST AREA (Exact same as Home) ---
            LazyColumn(
                modifier = modifier
                    .background(Color.Red)
            ) {
                items(transactions.filter { transaction ->
                    transaction.description.contains(query)
                }) { transaction ->
                    SwipableTransactionItem(
                        transaction = transaction,
                        isPeriodic = true,
                        onClick = {
                            navController.navigate(
                                FinancialManagerScreen.TransactionDetails.name + "/${transaction.uid}",
                            )
                        },
                        onDelete = { shouldDeleteParent ->
                            vm.deleteTransaction(
                                transaction = transaction,
                            )
                        }
                    )
                }
            }
        }
    }
}

