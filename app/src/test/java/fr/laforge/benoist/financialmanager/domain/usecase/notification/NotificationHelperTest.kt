package fr.laforge.benoist.financialmanager.domain.usecase.notification

import android.content.Context
import fr.laforge.benoist.financialmanager.infrastructure.helper.NotificationHelperImpl
import io.mockk.mockk
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class NotificationHelperTest {
    private val context: Context = mockk()
    private val notificationHelper = NotificationHelperImpl(context)

    @Test
    fun `invoke should return correct Transaction when receiving a google wallet notification`() {
        // --- Arrange ---
        val notificationTitle = "DUMMY VENDOR"
        val notificationMessage = "10,00 € dummy string"

        // --- Act ---
        val result = notificationHelper.parseNotificationMessage(notificationTitle, notificationMessage)

        // --- Assert ---
        result.isSuccess `should be` true
        val transaction = result.getOrNull()
        transaction?.amount `should be equal to` 10.00f
        transaction?.description `should be` notificationTitle
    }

    @Test
    fun `invoke should return failure when receiving negative amount notification`() {
        // --- Arrange ---
        val notificationTitle = "DUMMY VENDOR"
        val notificationMessage = "-10,00€ dummy string"

        // --- Act ---
        val result = notificationHelper.parseNotificationMessage(notificationTitle, notificationMessage)

        // --- Assert ---
        result.isFailure `should be` true
        val exception = result.exceptionOrNull()
        exception?.message `should be` "Negative amount"
    }
}
