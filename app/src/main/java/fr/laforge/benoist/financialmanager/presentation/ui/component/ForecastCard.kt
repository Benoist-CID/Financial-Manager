package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ForecastCard(
    projectedBalance: Float,
    modifier: Modifier = Modifier
) {
    val isPositive = projectedBalance >= 0
    val containerColor = if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE) // Light Green or Red
    val contentColor = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828) // Dark Green or Red
    val icon = if (isPositive) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Projected End-of-Month Balance",
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = contentColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${formatAmount(projectedBalance)} €",
                    style = MaterialTheme.typography.headlineLarge, // Big & Bold
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Text(
                text = if(isPositive) "You are on track to save money!" else "Warning: You might overspend.",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
