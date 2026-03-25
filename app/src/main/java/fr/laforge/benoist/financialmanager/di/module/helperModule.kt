package fr.laforge.benoist.financialmanager.di.module

import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.helper.NotificationHelperImpl
import fr.laforge.benoist.financialmanager.infrastructure.service.AndroidExportService
import fr.laforge.benoist.financialmanager.infrastructure.service.NotificationListenerHelper
import fr.laforge.benoist.financialmanager.presentation.util.ExportService
import org.koin.dsl.module

val helperModule by lazy {
    module {
        single<ExportService> {
            AndroidExportService(context = get())
        }
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