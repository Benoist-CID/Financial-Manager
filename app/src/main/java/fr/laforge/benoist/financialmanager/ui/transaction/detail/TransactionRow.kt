package fr.laforge.benoist.financialmanager.ui.transaction.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.laforge.benoist.financialmanager.R

@Composable
fun TransactionRow(
    labelId: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Text(
            text = stringResource(labelId)
        )

        Spacer(modifier = modifier.width(4.dp))
        Spacer(modifier = modifier.weight(1.0f))

        Text(
            text = text,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionRowPreview() {
    TransactionRow(
        labelId = R.string.app_name, // Replace with a real string resource ID
        text = "Sample Text"
    )
}
