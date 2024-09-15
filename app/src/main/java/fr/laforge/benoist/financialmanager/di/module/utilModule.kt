package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.util.AndroidNotificationUtil
import fr.laforge.benoist.financialmanager.util.NotificationUtil
import org.koin.dsl.module

val utilModule by lazy {
    module {
        factory<NotificationUtil> { AndroidNotificationUtil(context = get()) }
    }
}
