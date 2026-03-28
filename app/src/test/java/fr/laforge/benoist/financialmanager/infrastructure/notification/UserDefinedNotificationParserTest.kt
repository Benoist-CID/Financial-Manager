package fr.laforge.benoist.financialmanager.infrastructure.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class UserDefinedNotificationParserTest {

    // ----- canParse -----

    @Test
    fun `canParse returns true when body matches pattern with amount and description`() {
        val parser = parser("{amount} € {description}")
        parser.canParse("10,50 € Amazon Prime") `should be` true
    }

    @Test
    fun `canParse returns true when pattern has only amount placeholder`() {
        val parser = parser("Debit {amount}EUR")
        parser.canParse("Debit 25EUR") `should be` true
    }

    @Test
    fun `canParse returns false when literal separator is missing`() {
        val parser = parser("{amount} € {description}")
        parser.canParse("10,50 Amazon Prime") `should be` false
    }

    @Test
    fun `canParse returns false when amount position is not numeric`() {
        val parser = parser("{amount} €")
        parser.canParse("notanumber €") `should be` false
    }

    @Test
    fun `canParse returns false when body is empty`() {
        val parser = parser("{amount} € {description}")
        parser.canParse("") `should be` false
    }

    // ----- parse — happy path -----

    @Test
    fun `parse extracts amount and description separated by literal`() {
        val parser = parser("{amount} € {description}")
        val result = parser.parse("Title", "10,50 € Amazon Prime")

        result.isSuccess `should be` true
        val parsed = result.getOrNull()!!
        parsed.amount shouldBeEqualTo 10.5f
        parsed.description `should be equal to` "Amazon Prime"
        parsed.source `should be equal to` NotificationSource.CUSTOM
    }

    @Test
    fun `parse uses dot as decimal separator`() {
        val parser = parser("{amount} € {description}")
        val result = parser.parse("T", "9.99 € Coffee")
        result.getOrNull()!!.amount shouldBeEqualTo 9.99f
    }

    @Test
    fun `parse uses title as description when pattern has no {description}`() {
        val parser = parser("Payment {amount}EUR")
        val result = parser.parse("My Bank", "Payment 15EUR")

        result.isSuccess `should be` true
        result.getOrNull()!!.description `should be equal to` "My Bank"
    }

    @Test
    fun `parse handles amount at end of body`() {
        val parser = parser("{description}: {amount}")
        val result = parser.parse("T", "Supermarket: 32,00")

        result.isSuccess `should be` true
        val parsed = result.getOrNull()!!
        parsed.amount shouldBeEqualTo 32.0f
        parsed.description `should be equal to` "Supermarket"
    }

    // ----- parse — edge cases -----

    @Test
    fun `parse returns failure for negative amount`() {
        val parser = parser("{amount} €")
        val result = parser.parse("T", "-5 €")
        result.isFailure `should be` true
    }

    @Test
    fun `parse returns failure when body does not match pattern`() {
        val parser = parser("{amount} € {description}")
        val result = parser.parse("T", "completely different text")
        result.isFailure `should be` true
    }

    // ----- helper -----

    private fun parser(pattern: String) =
        UserDefinedNotificationParser(NotificationFormat(description = "Test", pattern = pattern))
}
