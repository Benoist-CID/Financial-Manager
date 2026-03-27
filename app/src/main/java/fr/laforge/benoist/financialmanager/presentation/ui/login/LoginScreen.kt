package fr.laforge.benoist.financialmanager.presentation.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.laforge.benoist.financialmanager.R
import fr.laforge.benoist.financialmanager.presentation.ui.FinancialManagerScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as FragmentActivity
    val vm: LoginViewModel = koinViewModel(parameters = { parametersOf(activity) })
    var displayBiometrics by remember {
        mutableStateOf(true)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
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

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                modifier = modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = {
                    navController.navigate(route = FinancialManagerScreen.Home.name)
                },
                modifier = modifier.size(60.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    modifier = modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.fingerprint),
                    contentDescription = "content description"
                )
            }
        }
    }

    if (displayBiometrics) {
        vm.authenticate(
            title = stringResource(id = R.string.biometric_title),
            subTitle = "",
            description = stringResource(id = R.string.biometric_subtitle),
            negativeButtonText = stringResource(id = R.string.biometric_negative),
            onAuthenticationOk = {
                displayBiometrics = false
                navController.navigate(route = FinancialManagerScreen.Home.name)
            },
            onAuthenticationFailed = {
                displayBiometrics = false
            }
        )
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}
