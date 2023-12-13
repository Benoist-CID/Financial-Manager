package fr.laforge.benoist.financialmanager.views.transaction.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.model.Transaction

@Composable
fun TransactionDetails(
    navController: NavController,
    vm: TransactionDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val transaction: Transaction by vm.transaction.collectAsState(initial = Transaction())
    Text("Transaction details: ${transaction.description}")
}

@Composable
@Preview(showBackground = true)
fun TransactionDetailsPreview() {
//    TransactionDetails(navController = rememberNavController())
}
