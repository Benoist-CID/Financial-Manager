package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class GooglePayNotificationParserTest {

    private val parser = GooglePayNotificationParser()

    // --- canParse ---

    @Test
    fun `canParse returns true for valid Google Pay format`() {
        parser.canParse("10,00 € dummy string") `should be` true
    }

    @Test
    fun `canParse returns false for bank format`() {
        parser.canParse("Starbucks€12,50") `should be` false
    }

    @Test
    fun `canParse returns false when no euro symbol present`() {
        parser.canParse("no euro here") `should be` false
    }

    // --- parse: happy path ---

    @Test
    fun `parse extracts amount and title correctly`() {
        val result = parser.parse("DUMMY VENDOR", "10,00 € dummy string")
        result.isSuccess `should be` true
        result.getOrNull()?.amount `should be equal to` 10.00f
        result.getOrNull()?.description `should be equal to` "DUMMY VENDOR"
        result.getOrNull()?.source `should be equal to` NotificationSource.GOOGLE_PAY
    }

    @Test
    fun `parse handles amount without trailing text`() {
        val result = parser.parse("Shop", "5,99€")
        result.isSuccess `should be` true
        result.getOrNull()?.amount `should be equal to` 5.99f
    }

    // --- parse: edge cases ---

    @Test
    fun `parse returns failure for negative amount`() {
        val result = parser.parse("VENDOR", "-10,00 € refund")
        result.isFailure `should be` true
    }

    @Test
    fun `parse returns failure when amount is not parseable`() {
        val result = parser.parse("VENDOR", "abc€rest")
        result.isFailure `should be` true
    }
}
