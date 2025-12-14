package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.domain.model.indicator.LifestyleState
import fr.laforge.benoist.financialmanager.domain.model.indicator.LifestyleStatus

@Composable
fun LifestyleRatioIndicator(
    state: LifestyleState,
    modifier: Modifier = Modifier
) {
    // 1. Determine Color & Label based on the Enum
    val color = when (state.status) {
        LifestyleStatus.Excellent -> Color(0xFF66BB6A) // Soft Green
        LifestyleStatus.Healthy -> Color(0xFF9CCC65)   // Yellow-Green
        LifestyleStatus.Heavy -> Color(0xFFFFCA28)     // Amber
        LifestyleStatus.Danger -> Color(0xFFEF5350)    // Red
    }

    val label = when (state.status) {
        LifestyleStatus.Excellent -> "Excellent (High Flexibility)"
        LifestyleStatus.Healthy -> "Healthy (Balanced)"
        LifestyleStatus.Heavy -> "Heavy (Tight Budget)"
        LifestyleStatus.Danger -> "Critical (Overloaded)"
    }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // --- Header Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Lifestyle Load (Fixed Costs)",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = "${(state.ratio * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        // --- Status Text ---
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // --- The Progress Bar ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2C2C)) // Dark background for the track
        ) {
            // A. Actual Progress Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(state.ratio.coerceIn(0f, 1f))
                    .background(color)
            )

            // B. Threshold Markers (Dynamic from Enum)
            // Draws a thin line at 30% (Excellent limit) and 50% (Healthy limit)
            MarkerLine(LifestyleStatus.Excellent.maxThreshold)
            MarkerLine(LifestyleStatus.Healthy.maxThreshold)
        }

        // --- Legend Row ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            // Dynamic Legend Text derived from Enum
            Text(
                text = "${(LifestyleStatus.Excellent.maxThreshold * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = "${(LifestyleStatus.Healthy.maxThreshold * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Text("100%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun BoxScope.MarkerLine(percent: Float) {
    Canvas(modifier = Modifier.matchParentSize()) {
        val x = size.width * percent
        drawLine(
            color = Color.Black.copy(alpha = 0.4f), // Semi-transparent black line
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 2.dp.toPx()
        )
    }
}
