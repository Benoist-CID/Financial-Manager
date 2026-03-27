package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be`
import org.junit.Test

class AddNotificationFormatUseCaseTest {

    private val repository = mockk<NotificationFormatRepository>()
    private val useCase = AddNotificationFormatUseCase(repository)

    // --- Happy path ---

    @Test
    fun `invoke saves format when pattern contains {amount}`() = runTest {
        // --- Arrange ---
        val format = NotificationFormat(
            description = "My bank",
            pattern = "{amount} € {description}",
        )
        coJustRun { repository.add(format) }

        // --- Act ---
        useCase(format)

        // --- Assert ---
        coVerify(exactly = 1) { repository.add(format) }
    }

    @Test
    fun `invoke saves format when pattern contains only {amount} and no {description}`() = runTest {
        // --- Arrange ---
        val format = NotificationFormat(description = "Simple", pattern = "Debit {amount}EUR")
        coJustRun { repository.add(format) }

        // --- Act ---
        useCase(format)

        // --- Assert ---
        coVerify(exactly = 1) { repository.add(format) }
    }

    // --- Edge cases ---

    @Test
    fun `invoke throws when pattern has no {amount} placeholder`() = runTest {
        // --- Arrange ---
        val format = NotificationFormat(description = "Bad", pattern = "{description} only")

        // --- Act ---
        var threw = false
        try {
            useCase(format)
        } catch (e: IllegalArgumentException) {
            threw = true
        }

        // --- Assert ---
        threw `should be` true
        coVerify(exactly = 0) { repository.add(any()) }
    }

    @Test
    fun `invoke throws when pattern is blank`() = runTest {
        // --- Arrange ---
        val format = NotificationFormat(description = "Empty", pattern = "")

        // --- Act ---
        var threw = false
        try {
            useCase(format)
        } catch (e: IllegalArgumentException) {
            threw = true
        }

        // --- Assert ---
        threw `should be` true
    }
}
