package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    title: String,
    transactions: List<Transaction>,
    totalMonthly: Float,
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onItemClick: (Transaction) -> Unit,
    onItemDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(paddingValues)
        ) {
            // --- HEADER AREA ---
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                // Total Summary
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

                // Search Component
                SearchComponent(
                    query = query,
                    onQueryChange = onQueryChange
                )
            }

            // --- LIST AREA ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                // Note: Removed Color.Red as it was likely for debug
            ) {
                // Filter logic handled here to match your previous snippets
                val filteredList = transactions.filter {
                    it.description.contains(query, ignoreCase = true)
                }

                items(filteredList, key = { it.uid }) { transaction ->
                    SwipableTransactionItem(
                        transaction = transaction,
                        isPeriodic = true,
                        onClick = { onItemClick(transaction) },
                        onDelete = { onItemDelete(transaction) }
                    )
                }
            }
        }
    }
}
