package fr.laforge.benoist.financialmanager.views.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.util.getNumberOfRemainingDaysInMonth
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SituationCard(
    amount: Float,
    periodicAmount: Float,
    modifier: Modifier = Modifier,
    date: LocalDateTime = LocalDateTime.now()
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$amount €", fontSize = 20.sp)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        Text(
            text = "${LocalContext.current.getString(R.string.situation_on)} ${date.format(formatter)}",
            fontSize = 10.sp
        )

        Text(
            text = "Total recurring expenses - $periodicAmount€",
            fontSize = 15.sp
        )

        Text(
            text = "${date.getNumberOfRemainingDaysInMonth()} days left - ${(amount / date.getNumberOfRemainingDaysInMonth()).toInt()}€ per day",
            fontSize = 17.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SituationCardPreview() {
    SituationCard(amount = 2000.0F, periodicAmount = 1000F)
}
