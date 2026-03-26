package fr.laforge.benoist.financialmanager.presentation.ui.login

import androidx.lifecycle.ViewModel
import fr.laforge.benoist.financialmanager.domain.usecase.BiometricAuthenticator

/**
 * ViewModel for the login screen.
 *
 * Delegates all biometric authentication to [BiometricAuthenticator], keeping this
 * class free of any Android biometric framework dependency and making it fully
 * unit-testable without an Android runtime.
 *
 * @property biometricAuthenticator Domain port that abstracts the biometric prompt.
 *
 * @note No Android framework types (`BiometricPrompt`, `FragmentActivity`, etc.) are
 * imported here by design — this enforces the Zero Framework Policy in the presentation
 * layer and ensures the ViewModel remains independently testable.
 */
class LoginViewModel(
    private val biometricAuthenticator: BiometricAuthenticator,
) : ViewModel() {

    /**
     * Triggers a biometric authentication challenge.
     *
     * @param title              Primary heading shown in the system dialog.
     * @param subTitle           Sub-heading shown below the title.
     * @param description        Body text of the dialog.
     * @param negativeButtonText Label for the negative/cancel button.
     * @param onAuthenticationOk     Invoked when the user authenticates successfully.
     * @param onAuthenticationFailed Invoked when authentication is denied or errors.
     */
    fun authenticate(
        title: String,
        subTitle: String,
        description: String,
        negativeButtonText: String,
        onAuthenticationOk: () -> Unit,
        onAuthenticationFailed: () -> Unit,
    ) {
        biometricAuthenticator.authenticate(
            title = title,
            subTitle = subTitle,
            description = description,
            negativeButtonText = negativeButtonText,
            onAuthenticationOk = onAuthenticationOk,
            onAuthenticationFailed = onAuthenticationFailed,
        )
    }
}
