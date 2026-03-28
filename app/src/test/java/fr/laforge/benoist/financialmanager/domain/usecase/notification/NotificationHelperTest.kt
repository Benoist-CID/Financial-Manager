package fr.laforge.benoist.financialmanager.domain.usecase.notification

import android.content.Context
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import fr.laforge.benoist.financialmanager.infrastructure.helper.NotificationHelperImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class NotificationHelperTest {
    private val context: Context = mockk()
    private val formatRepository: NotificationFormatRepository = mockk {
        every { getAll() } returns flowOf(emptyList())
    }
    private val notificationHelper = NotificationHelperImpl(
        context = context,
        formatRepository = formatRepository,
    )

    // --- isTransaction ---

    @Test
    fun `isTransaction returns true for Google Pay notification body`() {
        val result = notificationHelper.isTransaction("10,00 € dummy string")
        result.isSuccess `should be` true
        result.getOrNull() `should be equal to` true
    }

    @Test
    fun `isTransaction returns true for bank notification body`() {
        val result = notificationHelper.isTransaction("DUMMY VENDOR €10.00")
        result.isSuccess `should be` true
        result.getOrNull() `should be equal to` true
    }

    @Test
    fun `isTransaction returns false when body contains no euro sign`() {
        val result = notificationHelper.isTransaction("Breaking news: markets soar")
        result.isSuccess `should be` true
        result.getOrNull() `should be equal to` false
    }

    // --- parseToPending ---

    @Test
    fun `parseToPending returns ParsedNotification for Google Pay format`() {
        // Google Pay format: numeric amount before €, merchant in title
        val result = notificationHelper.parseToPending(
            notificationTitle = "DUMMY VENDOR",
            notificationMessage = "10,00 € dummy string",
        )

        result.isSuccess `should be` true
        val parsed = result.getOrNull()!!
        parsed.amount shouldBeEqualTo 10.00f
        parsed.description `should be equal to` "DUMMY VENDOR"
        parsed.source `should be equal to` NotificationSource.GOOGLE_PAY
    }

    @Test
    fun `parseToPending returns ParsedNotification for bank format`() {
        // Bank format: description before €, amount after €
        val result = notificationHelper.parseToPending(
            notificationTitle = "BankApp",
            notificationMessage = "DUMMY VENDOR €10.00",
        )

        result.isSuccess `should be` true
        val parsed = result.getOrNull()!!
        parsed.amount shouldBeEqualTo 10.00f
        parsed.description `should be equal to` "DUMMY VENDOR"
        parsed.source `should be equal to` NotificationSource.BANK
    }

    @Test
    fun `parseToPending returns failure when body has no parseable format`() {
        val result = notificationHelper.parseToPending(
            notificationTitle = "App",
            notificationMessage = "No amount here",
        )

        result.isFailure `should be` true
    }
}
