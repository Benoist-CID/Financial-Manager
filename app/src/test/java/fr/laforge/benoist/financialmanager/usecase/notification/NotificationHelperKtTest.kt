package fr.laforge.benoist.financialmanager.usecase.notification

import org.amshove.kluent.`should be equal to`
import org.junit.Test

class NotificationHelperKtTest {

    private val notificationHelper: NotificationHelper = NotificationHelperImpl()

    @Test
    fun `Tests isTransaction when the notification is a transaction`() {
        val message = "POINT P 1233 € 36,67"

        notificationHelper.isTransaction(notificationMessage = message).`should be equal to`(Result.success(true))
    }

    @Test
    fun `Tests isTransaction when the notification is not a transaction`() {
        val message = "POINT P 1233 36,67"

        notificationHelper.isTransaction(notificationMessage = message).`should be equal to`(Result.success(false))
    }

    @Test
    fun `Tests parseNotificationMessage with a valid message`() {
        val message = "POINT P 1233 € 36,67"

        val transaction = notificationHelper.parseNotificationMessage(notificationMessage = message)

        val expected = transaction.getOrNull()

        expected?.amount.`should be equal to`(36.67F)
        expected?.description.`should be equal to`("POINT P 1233")
    }
}
