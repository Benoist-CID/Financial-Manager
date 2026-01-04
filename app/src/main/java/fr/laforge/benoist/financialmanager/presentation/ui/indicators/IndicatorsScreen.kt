package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.BalanceHeroCard
import fr.laforge.benoist.financialmanager.presentation.ui.component.ForecastCard
import fr.laforge.benoist.financialmanager.presentation.ui.component.IndicatorBar
import fr.laforge.benoist.financialmanager.presentation.ui.component.LifestyleRatioIndicator
import fr.laforge.benoist.financialmanager.presentation.ui.component.formatAmount
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorsScreen(
    navController: NavController,
    vm: IndicatorsViewModel = koinViewModel(),
) {
    // 1. Collect Data
    val startOfMonthBalance by vm.startBalanceFlow.collectAsState()
    val recurringIncome by vm.recurringIncome.collectAsState()
    val nonRecurringIncome by vm.nonRecurringIncome.collectAsState()
    val recurringExpenses by vm.recurringExpenses.collectAsState()
    val regularExpenses by vm.regularExpenses.collectAsState()
    val projectedBalance by vm.projectedBalance.collectAsState()
    val lifestyleState by vm.lifestyleRatio.collectAsState()
    val graphData by vm.dailyBalanceGraph.collectAsState()

    // 2. Calcs
    val totalIncome = recurringIncome + nonRecurringIncome
    val totalExpenses = recurringExpenses + regularExpenses
    val remainingBalance = totalIncome - totalExpenses + startOfMonthBalance
    val maxReference = maxOf(totalIncome, totalExpenses).coerceAtLeast(1f)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.indicators),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- ZONE 1: THE HUD (Present) ---
            // "Where am I right now?"
            BalanceHeroCard(
                balance = remainingBalance,
                graphData = graphData
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- ZONE 2: THE FORECAST (Future) ---
            // "Where am I going?"
            ForecastCard(
                projectedBalance = projectedBalance,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            // --- ZONE 3: THE ENGINE ROOM (Details) ---
            // "Why is it like this?"

            // Group 1: Money In
            SectionHeader(title = stringResource(R.string.income))

            IndicatorBar(
                label = stringResource(R.string.recurring_income), // Salary
                amount = recurringIncome,
                totalReference = maxReference,
                color = Color(0xFF4CAF50),
                onClick = { navController.navigate(FinancialManagerScreen.RecurringIncome.name) }
            )
            if (nonRecurringIncome > 0) {
                IndicatorBar(
                    label = stringResource(R.string.variable_income), // Bonus
                    amount = nonRecurringIncome,
                    totalReference = maxReference,
                    color = Color(0xFF00E676),
                    onClick = { navController.navigate(FinancialManagerScreen.NonRecurringIncome.name) }
                )
            }
            // Small Summary line for Income
            TotalRow(label = "Total Income", amount = totalIncome, color = Color(0xFF4CAF50))

            Spacer(modifier = Modifier.height(24.dp))

            // Group 2: Money Out
            SectionHeader(title = stringResource(R.string.expenses))

            IndicatorBar(
                label = stringResource(R.string.recurring_expenses),
                amount = recurringExpenses,
                totalReference = maxReference,
                color = Color(0xFFFF9800),
                onClick = { navController.navigate(FinancialManagerScreen.RecurringExpenses.name) }
            )
            IndicatorBar(
                label = stringResource(R.string.label_variable_expenses),
                amount = regularExpenses,
                totalReference = maxReference,
                color = Color(0xFFF44336),
                onClick = { navController.navigate(FinancialManagerScreen.NonRecurringExpenses.name) }
            )
            // Small Summary line for Expenses
            TotalRow(
                label = stringResource(R.string.label_total_expenses),
                amount = totalExpenses,
                color = Color(0xFFD32F2F)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            // --- ZONE 4: STRATEGY ---
            // "Is my structure healthy?"
            LifestyleRatioIndicator(state = lifestyleState)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Helper Composable for clean headers
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary, // Pop color
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// Helper for clean totals without a full bar
@Composable
fun TotalRow(label: String, amount: Float, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(
            text = "${formatAmount(amount)} €",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
