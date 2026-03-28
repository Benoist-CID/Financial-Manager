package fr.laforge.benoist.financialmanager.presentation.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen

/**
 * Application top bar with search, an optional pending-transaction badge, and a settings icon.
 *
 * @param navController   Navigation controller for settings and pending-transactions routes.
 * @param query           Current search query displayed in [SearchComponent].
 * @param onQueryChange   Callback invoked when the user edits the search text.
 * @param pendingCount    Number of pending transactions awaiting user action.
 *   When greater than zero, a notification badge is shown on the bell icon.
 *   Defaults to 0 (no badge rendered).
 * @param onPendingClick  Callback invoked when the user taps the pending-transactions bell icon.
 *   Defaults to navigating to [FinancialManagerScreen.PendingTransactions].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    query: String,
    onQueryChange: (String) -> Unit,
    pendingCount: Int = 0,
    onPendingClick: () -> Unit = { navController.navigate(FinancialManagerScreen.PendingTransactions.name) },
) {
    TopAppBar(
        title = {
            SearchComponent(
                query = query,
                onQueryChange = onQueryChange,
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            if (pendingCount > 0) {
                IconButton(onClick = onPendingClick) {
                    BadgedBox(
                        badge = {
                            Badge {
                                Text(
                                    text = pendingCount.coerceAtMost(99).toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Pending transactions: $pendingCount",
                        )
                    }
                }
            }
            IconButton(onClick = { navController.navigate(FinancialManagerScreen.Settings.name) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.settings),
                )
            }
        },
    )
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(rememberNavController(), "TITLE", onQueryChange = {})
}

@Preview
@Composable
fun TopBarWithBadgePreview() {
    TopBar(rememberNavController(), "", onQueryChange = {}, pendingCount = 3)
}
