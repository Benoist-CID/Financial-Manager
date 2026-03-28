package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class BankNotificationParserTest {

    private val parser = BankNotificationParser()

    // --- canParse ---

    @Test
    fun `canParse returns true for valid bank format`() {
        parser.canParse("Starbucks€12,50") `should be` true
    }

    @Test
    fun `canParse returns false for Google Pay format`() {
        parser.canParse("10,00 € dummy string") `should be` false
    }

    @Test
    fun `canParse returns false when no euro symbol present`() {
        parser.canParse("no euro here") `should be` false
    }

    // --- parse: happy path ---

    @Test
    fun `parse extracts description and amount correctly`() {
        val result = parser.parse("ignored title", "Starbucks€12,50")
        result.isSuccess `should be` true
        result.getOrNull()?.amount `should be equal to` 12.50f
        result.getOrNull()?.description `should be equal to` "Starbucks"
        result.getOrNull()?.source `should be equal to` NotificationSource.BANK
    }

    @Test
    fun `parse trims whitespace from description and amount`() {
        val result = parser.parse("ignored", " Coffee Shop €3,00")
        result.isSuccess `should be` true
        result.getOrNull()?.description `should be equal to` "Coffee Shop"
        result.getOrNull()?.amount `should be equal to` 3.00f
    }

    // --- parse: edge cases ---

    @Test
    fun `parse returns failure for negative amount`() {
        val result = parser.parse("ignored", "Starbucks€-12,50")
        result.isFailure `should be` true
    }

    @Test
    fun `parse returns failure when amount is missing`() {
        val result = parser.parse("ignored", "NoAmountHere")
        result.isFailure `should be` true
    }
}
