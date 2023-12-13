package fr.laforge.benoist.financialmanager.views.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.laforge.benoist.financialmanager.AppViewModelProvider
import fr.laforge.benoist.financialmanager.FinancialManagerScreen
import fr.laforge.benoist.financialmanager.R

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var displayBiometrics by remember{
        mutableStateOf(true)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                displayBiometrics = true
            }
        }
    }

    Text("Login screen")

    if (displayBiometrics) {
        vm.displayBiometricAuthenticator(
            activity = LocalContext.current as FragmentActivity,
            title = stringResource(id = R.string.biometric_title),
            subTitle = "",
            description = stringResource(id = R.string.biometric_subtitle),
            negativeButtonText = stringResource(id = R.string.biometric_negative),
            {
                displayBiometrics = false
                // Navigate to next screen
                navController.navigate(route = FinancialManagerScreen.Home.name)
            },
            {
                displayBiometrics = false
                // Display error
            }
        )
    }
}