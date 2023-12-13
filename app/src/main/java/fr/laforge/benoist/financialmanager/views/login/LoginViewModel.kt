package fr.laforge.benoist.financialmanager.views.login

import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import timber.log.Timber

class LoginViewModel : ViewModel() {
    /**
     * Displays a biometric authentication dialog
     */
    fun displayBiometricAuthenticator(
        activity: FragmentActivity,
        title: String,
        subTitle: String,
        description: String,
        negativeButtonText: String,
        onAuthenticationOk: () -> Unit,
        onAuthenticationFailed: () -> Unit
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subTitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .build()
        val biometricPrompt = BiometricPrompt(activity, {
            if (Looper.myLooper() == null) {
                Looper.prepare()
            }
            it.run()
        }, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Timber.e("Attempt to authenticate the user has failed $errorCode - $errString")
                onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.d("User authentication succeeded")
                onAuthenticationOk()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.e("Attempt to authenticate the user has failed")
                onAuthenticationFailed()
            }
        }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
