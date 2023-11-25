package fr.laforge.benoist.financialmanager.views.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SituationCard(
    amount: Float,
    modifier: Modifier = Modifier,
    date: LocalDate = LocalDate.now()
) {
    Card {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$amount €")
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

            Text(
                text = "${LocalContext.current.getString(R.string.situation_on)} ${date.format(formatter)}",
                fontSize = 10.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SituationCardPreview() {
    SituationCard(amount = 2000.0F)
}
