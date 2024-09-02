package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.usecase.notification.NotificationHelperImpl
import org.koin.dsl.module

val helperModule by lazy {
    module {
        factory<NotificationHelper> { NotificationHelperImpl() }
    }
}