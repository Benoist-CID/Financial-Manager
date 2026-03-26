package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.domain.model.indicator.DailyPoint
import fr.laforge.benoist.financialmanager.presentation.ui.theme.BalanceNegativeRed
import fr.laforge.benoist.financialmanager.presentation.ui.theme.BalancePositiveBlue
import fr.laforge.benoist.financialmanager.presentation.ui.theme.DarkSurface

@Composable
fun BalanceHeroCard(
    balance: Float,
    graphData: List<DailyPoint>,
    modifier: Modifier = Modifier
) {
    val isPositive = balance >= 0
    val color = if (isPositive) BalancePositiveBlue else BalanceNegativeRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp), // Compact but tall enough for the graph
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. The Graph (Background Layer)
            // We pass a lighter color to the graph so it doesn't fight the text
            if (graphData.isNotEmpty()) {
                BalanceGraph(
                    data = graphData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp), // Push graph down slightly
                    lineColor = color
                )
            }

            // 2. The Text Content (Foreground Layer)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = stringResource(R.string.label_remaining_balance).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatAmount(balance)} €",
                    style = MaterialTheme.typography.displaySmall, // Big and Bold
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // 3. Optional: "Today" Indicator
            if (graphData.isNotEmpty()) {
                Text(
                    text = "Day ${graphData.last().dayOfMonth}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}
