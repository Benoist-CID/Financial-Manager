package fr.laforge.benoist.financialmanager.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.laforge.benoist.financialmanager.R

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    vm: SettingsViewModel = SettingsViewModel()
) {
    val savingsTarget by vm.savingsTarget.collectAsState(initial = 0F)

    Column {
        Text(
            text = stringResource(id = R.string.settings),
            fontSize = 30.sp,
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                bottom = 40.dp
            )
        )

        OutlinedTextField(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = "$savingsTarget",
            onValueChange = { vm.setSavingsTarget(it.toFloat()) },
            label = {
                Text(
                    text = stringResource(id = R.string.savings_target),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SettingsScreenPreview() {
    SettingsScreen()
}
