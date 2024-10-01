package fr.laforge.benoist.financialmanager.ui.transaction.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.ui.component.DatePickerComponent
import fr.laforge.benoist.financialmanager.util.next
import fr.laforge.benoist.model.TransactionPeriod
import timber.log.Timber
import java.time.LocalDateTime

@Composable
fun PeriodicalTransactionComponent(
    modifier: Modifier = Modifier,
    defaultValue: Boolean = false,
    defaultPeriod: TransactionPeriod = TransactionPeriod.Monthly,
    defaultStartDate: LocalDateTime = LocalDateTime.now(),
    onCheckedChange: (Boolean) -> Unit,
    onPeriodChanged: (TransactionPeriod) -> Unit,
    onStartDateChanged: (LocalDateTime) -> Unit
) {
    var checked by remember {
        mutableStateOf(defaultValue)
    }

    var period by remember {
        mutableStateOf(TransactionPeriod.Monthly)
    }

    var visible by remember {
        mutableStateOf(defaultValue)
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    checked = isChecked
                    visible = isChecked
                    onCheckedChange(isChecked)
                },
            )

            Text(text = stringResource(id = R.string.periodic))
        }

        // Animated visibility will eventually remove the item from the composition once the animation has finished.
        AnimatedVisibility(visible) {
            Column {
                GenericTypeDropdownMenu(
                    title = stringResource(R.string.period),
                    data = TransactionPeriod.values().map { it.toString() },
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    defaultValue = TransactionPeriod.Monthly.name
                ) { newPeriod ->
                    period = TransactionPeriod.from(newPeriod)!!
                    onPeriodChanged(
                        TransactionPeriod.from(newPeriod)!!
                    )
                }

                DatePickerComponent(
                    label = stringResource(R.string.start_date),
                    defaultDate = defaultStartDate
                ) {
                    Timber.i("$it")
                    onStartDateChanged(it)
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PeriodicalTransactionComponentPreview() {
    PeriodicalTransactionComponent(
        defaultValue = true,
        onCheckedChange = {},
        onPeriodChanged = {},
        onStartDateChanged = {}
    )
}
