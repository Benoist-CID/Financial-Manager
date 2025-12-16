package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.BalanceGraph
import fr.laforge.benoist.financialmanager.presentation.ui.component.ForecastCard
import fr.laforge.benoist.financialmanager.presentation.ui.component.IndicatorBar
import fr.laforge.benoist.financialmanager.presentation.ui.component.LifestyleRatioIndicator
import org.koin.androidx.compose.koinViewModel

@Composable
fun IndicatorsScreen(
    navController: NavController,
    vm: IndicatorsViewModel = koinViewModel(),
) {
    val recurringIncome by vm.recurringIncome.collectAsState(initial = 0f)
    val recurringExpenses by vm.recurringExpenses.collectAsState(initial = 0f)
    val regularExpenses by vm.regularExpenses.collectAsState(initial = 0f)
    val projectedBalance by vm.projectedBalance.collectAsState()
    val lifestyleState by vm.lifestyleRatio.collectAsState()
    val graphData by vm.dailyBalanceGraph.collectAsState()

    val totalIncome = recurringIncome
    val totalExpenses = recurringExpenses + regularExpenses
    val remainingBalance = totalIncome - totalExpenses
    val maxReference = if (totalIncome > 0) totalIncome else 1f

    Scaffold(
        topBar = {
            Text(
                text = stringResource(R.string.indicators),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- Section 1: Money In ---
            Text(
                text = stringResource(R.string.income),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            IndicatorBar(
                label = stringResource(R.string.recurring_income),
                amount = totalIncome,
                totalReference = maxReference,
                color = Color(0xFF4CAF50) // Green
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- Section 2: Money Out ---
            Text(
                text = stringResource(R.string.expenses),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            IndicatorBar(
                label = stringResource(R.string.recurring_expenses),
                amount = recurringExpenses,
                totalReference = maxReference,
                color = Color(0xFFFF9800),
            ) {
                navController.navigate(
                    FinancialManagerScreen.RecurringExpenses.name
                )
            }

            IndicatorBar(
                label = stringResource(R.string.label_variable_expenses),
                amount = regularExpenses,
                totalReference = maxReference,
                color = Color(0xFFF44336) // Red
            )

            IndicatorBar(
                label = stringResource(R.string.label_total_expenses),
                amount = totalExpenses,
                totalReference = maxReference,
                color = Color(0xFFD32F2F) // Darker Red
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- Section 3: Result ---
            Text(
                text = stringResource(R.string.section_result),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            if (graphData.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp), // Fixed height
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // Dark Background
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        BalanceGraph(
                            data = graphData,
                            modifier = Modifier.fillMaxSize(),
                            lineColor = if ((graphData.lastOrNull()?.balance ?: 0f) >= 0) Color(0xFF2196F3) else Color(0xFFEF5350)
                        )

                        // Small label for current balance on graph
                        Text(
                            text = "${stringResource(R.string.day)} ${graphData.last().dayOfMonth}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            val balanceColor = if (remainingBalance >= 0) Color(0xFF2196F3) else Color.Red

            IndicatorBar(
                label = stringResource(R.string.label_remaining_balance),
                amount = remainingBalance,
                totalReference = maxReference,
                color = balanceColor
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- Section 4: Forecast ---
            ForecastCard(
                projectedBalance = projectedBalance,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Section 5: Structural Health ---
            LifestyleRatioIndicator(state = lifestyleState)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun IndicatorsScreenPreview() {
//    IndicatorsScreen(
//        income = 1000F,
//        recurringExpenses = 250F,
//        regularExpenses = 300F,
//    )
}
