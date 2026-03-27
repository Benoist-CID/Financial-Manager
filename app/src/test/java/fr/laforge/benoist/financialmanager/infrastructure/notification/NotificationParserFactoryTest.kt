package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class NotificationParserFactoryTest {

    private val factory = NotificationParserFactory()

    // --- canParse ---

    @Test
    fun `canParse returns true for Google Pay format`() {
        factory.canParse("10,00 € some text") `should be` true
    }

    @Test
    fun `canParse returns true for bank format`() {
        factory.canParse("Starbucks€12,50") `should be` true
    }

    @Test
    fun `canParse returns false for unrecognised format`() {
        factory.canParse("no euro sign here") `should be` false
    }

    // --- parse: routes to correct parser ---

    @Test
    fun `parse routes Google Pay notification to GooglePayNotificationParser`() {
        val result = factory.parse("VENDOR", "10,00 € some text")
        result.isSuccess `should be` true
        result.getOrNull()?.source `should be equal to` NotificationSource.GOOGLE_PAY
    }

    @Test
    fun `parse routes bank notification to BankNotificationParser`() {
        val result = factory.parse("ignored", "Starbucks€12,50")
        result.isSuccess `should be` true
        result.getOrNull()?.source `should be equal to` NotificationSource.BANK
    }

    @Test
    fun `parse returns failure for unrecognised format`() {
        val result = factory.parse("title", "no euro sign here")
        result.isFailure `should be` true
    }
}
