package fr.laforge.benoist.financialmanager.presentation.ui.login

import fr.laforge.benoist.financialmanager.domain.usecase.BiometricAuthenticator
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class LoginViewModelTest {

    private val biometricAuthenticator = mockk<BiometricAuthenticator>(relaxed = true)
    private val viewModel = LoginViewModel(biometricAuthenticator)

    // --- Happy Path ---

    @Test
    fun `authenticate should delegate all parameters to BiometricAuthenticator`() {
        // --- Arrange ---
        val onOk = mockk<() -> Unit>(relaxed = true)
        val onFailed = mockk<() -> Unit>(relaxed = true)

        // --- Act ---
        viewModel.authenticate(
            title = "Login",
            subTitle = "Subtitle",
            description = "Please authenticate",
            negativeButtonText = "Cancel",
            onAuthenticationOk = onOk,
            onAuthenticationFailed = onFailed,
        )

        // --- Assert ---
        verify(exactly = 1) {
            biometricAuthenticator.authenticate(
                title = "Login",
                subTitle = "Subtitle",
                description = "Please authenticate",
                negativeButtonText = "Cancel",
                onAuthenticationOk = onOk,
                onAuthenticationFailed = onFailed,
            )
        }
    }

    // --- Edge Cases ---

    @Test
    fun `authenticate should invoke onAuthenticationOk callback when authenticator succeeds`() {
        // --- Arrange ---
        var okCalled = false
        val onOkSlot = slot<() -> Unit>()

        io.mockk.every {
            biometricAuthenticator.authenticate(any(), any(), any(), any(), capture(onOkSlot), any())
        } answers {
            onOkSlot.captured.invoke()
        }

        // --- Act ---
        viewModel.authenticate(
            title = "Login",
            subTitle = "",
            description = "",
            negativeButtonText = "Cancel",
            onAuthenticationOk = { okCalled = true },
            onAuthenticationFailed = {},
        )

        // --- Assert ---
        okCalled shouldBeEqualTo true
    }

    @Test
    fun `authenticate should invoke onAuthenticationFailed callback when authenticator fails`() {
        // --- Arrange ---
        var failedCalled = false
        val onFailedSlot = slot<() -> Unit>()

        io.mockk.every {
            biometricAuthenticator.authenticate(any(), any(), any(), any(), any(), capture(onFailedSlot))
        } answers {
            onFailedSlot.captured.invoke()
        }

        // --- Act ---
        viewModel.authenticate(
            title = "Login",
            subTitle = "",
            description = "",
            negativeButtonText = "Cancel",
            onAuthenticationOk = {},
            onAuthenticationFailed = { failedCalled = true },
        )

        // --- Assert ---
        failedCalled shouldBeEqualTo true
    }
}
