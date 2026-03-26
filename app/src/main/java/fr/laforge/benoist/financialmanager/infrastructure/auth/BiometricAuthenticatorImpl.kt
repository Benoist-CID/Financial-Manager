package fr.laforge.benoist.financialmanager.infrastructure.auth

import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import fr.laforge.benoist.financialmanager.domain.usecase.BiometricAuthenticator
import timber.log.Timber

/**
 * Android implementation of [BiometricAuthenticator] backed by [BiometricPrompt].
 *
 * Lives in the infrastructure layer so that all Android biometric framework imports
 * are isolated here, away from the domain and presentation layers.
 *
 * @property activity The [FragmentActivity] required by [BiometricPrompt] to attach
 *   the authentication dialog. Provided via Koin parametric injection from the
 *   composable that owns the current activity context.
 *
 * @note [BiometricPrompt] requires a live [FragmentActivity]; this class must therefore
 * be registered as a Koin `factory` (not a `single`) so a fresh instance is created
 * for each activity lifecycle. Registering as a singleton would risk holding a stale
 * activity reference after recreation.
 */
class BiometricAuthenticatorImpl(
    private val activity: FragmentActivity,
) : BiometricAuthenticator {

    /**
     * Builds and displays a [BiometricPrompt] dialog.
     *
     * @param title             Primary heading shown in the system dialog.
     * @param subTitle          Sub-heading shown below the title.
     * @param description       Body text of the dialog.
     * @param negativeButtonText Label for the negative/cancel button.
     * @param onAuthenticationOk    Called on successful biometric verification.
     * @param onAuthenticationFailed Called on any authentication failure or error.
     *
     * @note A [Looper] is prepared for the executor thread if not already present,
     * matching the original workaround required by certain device implementations.
     */
    override fun authenticate(
        title: String,
        subTitle: String,
        description: String,
        negativeButtonText: String,
        onAuthenticationOk: () -> Unit,
        onAuthenticationFailed: () -> Unit,
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subTitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            { runnable ->
                if (Looper.myLooper() == null) Looper.prepare()
                runnable.run()
            },
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Timber.e("Biometric authentication error $errorCode — $errString")
                    onAuthenticationFailed()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.d("Biometric authentication succeeded")
                    onAuthenticationOk()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Timber.e("Biometric authentication failed")
                    onAuthenticationFailed()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
