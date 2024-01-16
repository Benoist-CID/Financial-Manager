package fr.laforge.benoist.financialmanager.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.MainActivity
import fr.laforge.benoist.financialmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    title: String = "",
    onSave: () -> Unit,
    onLoad: () -> Unit
) {
    TopAppBar(
        title = { Text(
            text = title,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimary) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        actions = {
            IconButton(onClick = { onSave() }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { onLoad() }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.database),
                    contentDescription = "Load",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { navController.navigate(FinancialManagerScreen.AddInput.name) }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { navController.navigate(FinancialManagerScreen.Settings.name) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.settings),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(rememberNavController(), "TITLE", {}, {})
}
