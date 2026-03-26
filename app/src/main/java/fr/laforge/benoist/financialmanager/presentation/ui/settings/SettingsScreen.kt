package fr.laforge.benoist.financialmanager.presentation.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.presentation.ui.component.settings.SettingsActionItem
import fr.laforge.benoist.financialmanager.presentation.ui.component.settings.SettingsSectionTitle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val savingsTarget by vm.savingsTarget.collectAsState(initial = 0F)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            SettingsSectionTitle(titleId = R.string.budget)

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

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))

            SettingsSectionTitle(titleId = R.string.database)

            // 1. Export all transactions
            SettingsActionItem(
                icon = Icons.Filled.KeyboardArrowDown,
                titleId = R.string.export_db,
                descriptionId = R.string.export_db_description,
                onClick = { vm.saveDb() }
            )

            // 2. Export recurring transactions only
            SettingsActionItem(
                icon = Icons.Filled.KeyboardArrowDown,
                titleId = R.string.export_recurring_db,
                descriptionId = R.string.export_recurring_db_description,
                onClick = { vm.saveRecurringDb() }
            )

            // 3. Import Button
            SettingsActionItem(
                icon = ImageVector.vectorResource(id = R.drawable.database),
                titleId = R.string.import_db,
                descriptionId = R.string.import_db_description,
                isDestructive = false,
                onClick = { navController.navigate(FinancialManagerScreen.ImportDb.name) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}
