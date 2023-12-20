package fr.laforge.benoist.financialmanager.views.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            modifier = modifier.padding(bottom = 16.dp),
            fontSize = 25.sp
        )
        // Creating an Outlined Button and setting
        // the shape attribute to CircleShape
        // When the Button is clicked, a Toast
        // message would be displayed
        Button(onClick = { displayBiometrics = true },
            modifier = modifier.size(60.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) {
            // Adding an Icon "Add" inside the Button
            Icon(
                modifier = modifier.size(40.dp),
                painter = painterResource(id = R.drawable.fingerprint),
                contentDescription = "content description"
            )
        }
    }

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

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}