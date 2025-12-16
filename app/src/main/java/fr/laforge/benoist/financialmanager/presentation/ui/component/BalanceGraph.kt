package fr.laforge.benoist.financialmanager.presentation.ui.component

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.domain.model.indicator.DailyPoint
import java.time.LocalDate

@Composable
fun BalanceGraph(
    data: List<DailyPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF2196F3)
) {
    if (data.isEmpty()) return

    // 1. Setup Scales & Dimensions
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.GRAY
            textAlign = Paint.Align.CENTER
            textSize = density.run { 10.sp.toPx() } // Text size
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    val maxBalance = data.maxOf { it.balance }.coerceAtLeast(100f)
    val minBalance = data.minOf { it.balance }.coerceAtMost(0f)
    val balanceRange = maxBalance - minBalance
    val range = if (balanceRange == 0f) 1f else balanceRange

    // We assume the graph always covers the full month (e.g., 30 or 31 days)
    val daysInMonth = LocalDate.now().lengthOfMonth()

    // 2. Margins for Axes
    val bottomMargin = 40f // Space for day labels
    val topMargin = 20f    // Space for top dot

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height - bottomMargin // Reserve space at bottom

        // Coordinate Transformers
        // We leave a little padding on sides (16f) so text doesn't cut off
        fun x(day: Int): Float = 16f + ((day - 1).toFloat() / (daysInMonth - 1)) * (width - 32f)
        fun y(balance: Float): Float = topMargin + (height - topMargin) - ((balance - minBalance) / range * (height - topMargin))

        // 3. Build the Line Path
        val strokePath = Path()
        val fillPath = Path()

        if (data.isNotEmpty()) {
            val firstPoint = data.first()
            val startX = x(firstPoint.dayOfMonth)
            val startY = y(firstPoint.balance)

            strokePath.moveTo(startX, startY)
            fillPath.moveTo(startX, height)
            fillPath.lineTo(startX, startY)

            for (i in 1 until data.size) {
                val point = data[i]
                val nextX = x(point.dayOfMonth)
                val nextY = y(point.balance)
                strokePath.lineTo(nextX, nextY)
                fillPath.lineTo(nextX, nextY)
            }

            val lastPoint = data.last()
            fillPath.lineTo(x(lastPoint.dayOfMonth), height)
            fillPath.close()
        }

        // 4. Draw X-Axis Line
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )

        // 5. Draw Zero Line (Reference)
        if (minBalance < 0 && maxBalance > 0) {
            val zeroY = y(0f)
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(0f, zeroY),
                end = Offset(width, zeroY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }

        // 6. Draw Content (Gradient + Line)
        val brush = Brush.verticalGradient(
            colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
            startY = 0f,
            endY = height
        )
        drawPath(fillPath, brush)
        drawPath(
            strokePath,
            lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 7. Draw "Today" Point
        val lastPoint = data.last()
        val lastX = x(lastPoint.dayOfMonth)
        val lastY = y(lastPoint.balance)
        drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(lastX, lastY))
        drawCircle(lineColor, radius = 3.dp.toPx(), center = Offset(lastX, lastY))

        // 8. Draw X-Axis Labels (Days) using nativeCanvas
        drawContext.canvas.nativeCanvas.apply {
            // Draw labels every 5 days (1, 5, 10, 15, 20, 25, 30)
            val step = 5
            for (day in 1..daysInMonth step step) {
                val textX = x(day)
                val textY = height + 30f // Position below axis line

                // Optional: Highlight "Today" or specific days
                drawText(
                    day.toString(),
                    textX,
                    textY,
                    textPaint
                )
            }

            // Always ensure the very last day of month is drawn if not covered
            if (daysInMonth % step != 1) {
                drawText(
                    daysInMonth.toString(),
                    x(daysInMonth),
                    height + 30f,
                    textPaint
                )
            }
        }
    }
}
