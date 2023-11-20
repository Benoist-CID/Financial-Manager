package fr.laforge.benoist.financialmanager.ui.transaction

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TransactionDetails(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Text("Transaction details")
}

@Composable
@Preview(showBackground = true)
fun TransactionDetailsPreview() {
    TransactionDetails(navController = rememberNavController())
}
