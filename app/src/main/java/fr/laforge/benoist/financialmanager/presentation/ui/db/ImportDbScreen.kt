package fr.laforge.benoist.financialmanager.presentation.ui.db

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun ImportDbScreen(
    modifier: Modifier = Modifier,
    vm: ImportDbViewModel = koinViewModel(),
) {
    Column {
        Button(
            onClick = { vm.importDb() },
            modifier = modifier.fillMaxWidth()
        ) {
            Text(text = "Import DB")
        }
        OutlinedTextField(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = vm.toBeImported,
            onValueChange = { newValue -> vm.updateToBeImported(newValue) },
            label = {
                Text(
                    text = stringResource(id = R.string.description),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
        )
    }
}

@Preview
@Composable
fun ImportDbScreenPreview() {
    ImportDbScreen()
}
