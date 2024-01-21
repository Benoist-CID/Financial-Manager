package fr.laforge.benoist.financialmanager.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.component.AnimatedCircle
import fr.laforge.benoist.financialmanager.ui.component.formatAmount
import fr.laforge.benoist.financialmanager.util.getNumberOfRemainingDaysInMonth
import fr.laforge.benoist.util.getProportions
import java.time.LocalDateTime

@Composable
fun SituationCard(
    regularExpenses: Float,
    recurringExpenses: Float,
    income: Float,
    savingsTarget: Float,
    modifier: Modifier = Modifier,
    date: LocalDateTime = LocalDateTime.now()
) {
    val remaining = income - recurringExpenses - regularExpenses - savingsTarget

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        AnimatedCircle(
            proportions = getProportions(income, recurringExpenses, regularExpenses, savingsTarget),
            colors = listOf(
                colorResource(R.color.green_3),
                colorResource(R.color.orange_3),
                colorResource(R.color.red_3),
                colorResource(R.color.blue_4)
            ),
            modifier = modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Column(modifier = modifier.align(Alignment.Center)) {
            Text(
                text = "${formatAmount(remaining)} €",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = modifier.height(20.dp))

            Text(
                text = "${formatAmount(remaining / date.getNumberOfRemainingDaysInMonth())}€ " + stringResource(
                    id = R.string.per_day
                ),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SituationCardPreview() {
    SituationCard(regularExpenses = 2000.0F, recurringExpenses = 200F, savingsTarget = 500F, income = 1000F)
}
