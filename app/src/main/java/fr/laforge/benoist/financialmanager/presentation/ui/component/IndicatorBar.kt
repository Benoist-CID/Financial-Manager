package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IndicatorBar(
    label: String,
    amount: Float,
    totalReference: Float, // The value used to calculate the bar's width (usually Income)
    color: Color,
    onClick: (() -> Unit)? = null,
) {
    // Safety check to avoid division by zero
    val percentage = if (totalReference > 0) (amount / totalReference).coerceIn(0f, 1f) else 0f

    // Animation for smooth entry
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000),
        label = "ProgressBarAnimation"
    )

    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)) // Clip ripple to shape
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp) // Padding inside the click area
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    }

    Column(
        modifier = modifier
    ) {
        // Top Row: Label and Amount
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${formatAmount(amount)} €", // Assuming you have this helper
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Row: The Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(50) // Pill shape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress) // Width based on percentage
                    .fillMaxHeight()
                    .background(
                        color = color,
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun IndicatorBarPreview() {
    IndicatorBar(
        label = "Income",
        amount = 1745f,
        totalReference = 5745f,
        color = Color.Blue,
    )
}
