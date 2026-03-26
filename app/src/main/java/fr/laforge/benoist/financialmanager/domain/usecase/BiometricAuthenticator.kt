package fr.laforge.benoist.financialmanager.domain.usecase

/**
 * Port for triggering a biometric authentication challenge.
 *
 * Defining this boundary in the domain layer keeps ViewModels and business logic
 * free of any Android biometric framework dependency, making them fully unit-testable
 * without an Android runtime.
 *
 * The concrete implementation lives in the infrastructure layer and is injected at
 * runtime via the DI container.
 */
interface BiometricAuthenticator {

    /**
     * Presents a biometric prompt to the user and reports the outcome via callbacks.
     *
     * @param title             Primary text shown at the top of the prompt dialog.
     * @param subTitle          Secondary text shown below the title.
     * @param description       Longer explanation displayed in the prompt body.
     * @param negativeButtonText Label for the cancel / fallback button.
     * @param onAuthenticationOk    Invoked when the user is successfully authenticated.
     * @param onAuthenticationFailed Invoked when authentication is denied or encounters an error.
     *
     * @note Both callbacks are guaranteed to be called on the thread that the underlying
     * platform executor dispatches to. Callers should not assume a particular thread.
     */
    fun authenticate(
        title: String,
        subTitle: String,
        description: String,
        negativeButtonText: String,
        onAuthenticationOk: () -> Unit,
        onAuthenticationFailed: () -> Unit,
    )
}
