package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun IndicatorsScreen(modifier: Modifier = Modifier) {
    Text("Indicators")
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun IndicatorsScreenPreview() {
    IndicatorsScreen()
}
