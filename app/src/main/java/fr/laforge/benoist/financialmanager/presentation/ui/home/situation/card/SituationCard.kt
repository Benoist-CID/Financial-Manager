package fr.laforge.benoist.financialmanager.presentation.ui.home.situation.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.component.AnimatedCircle
import fr.laforge.benoist.financialmanager.presentation.ui.component.formatAmount
import fr.laforge.benoist.financialmanager.domain.util.getNumberOfRemainingDaysInMonth
import fr.laforge.benoist.financialmanager.domain.util.getProportions
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Composable
fun SituationCard(
    modifier: Modifier = Modifier,
    vm: SituationCardViewModel = koinViewModel(),
    date: LocalDateTime = LocalDateTime.now(),
    onClick: () -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(top = 8.dp)
    ) {
        AnimatedCircle(
            proportions = state.proportions, // Pre-calculated!
            colors = listOf(
                colorResource(R.color.green_3),
                colorResource(R.color.red_3),
                colorResource(R.color.orange_3),
                colorResource(R.color.blue_4)
            ),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "${formatAmount(state.remainingBalance)} €",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${formatAmount(state.dailyBudget)}€ " + stringResource(id = R.string.per_day),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SituationCardPreview() {
    SituationCard {

    }
}
