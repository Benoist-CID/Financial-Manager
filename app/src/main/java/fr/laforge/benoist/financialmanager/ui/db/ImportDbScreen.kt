package fr.laforge.benoist.financialmanager.ui.db

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.AppViewModelProvider
import fr.laforge.benoist.financialmanager.R

@Composable
fun ImportDbScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: ImportDbViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column {

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        val context = LocalContext.current

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
    ImportDbScreen(navController = rememberNavController())
}
