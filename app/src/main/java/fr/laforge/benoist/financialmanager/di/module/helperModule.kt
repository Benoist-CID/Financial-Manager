package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.helper.NotificationHelperImpl
import fr.laforge.benoist.financialmanager.infrastructure.service.NotificationListenerHelper
import org.koin.dsl.module

val helperModule by lazy {
    module {
        single<NotificationHelper> {
            NotificationHelperImpl(
                context = get()
            )
        }

        single {
            NotificationListenerHelper()
        }
    }
}