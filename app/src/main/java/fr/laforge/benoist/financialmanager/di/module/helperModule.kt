package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.interactors.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.interactors.notification.NotificationHelperImpl
import org.koin.dsl.module

val helperModule by lazy {
    module {
        factory<NotificationHelper> { NotificationHelperImpl(notificationUtil = get()) }
    }
}