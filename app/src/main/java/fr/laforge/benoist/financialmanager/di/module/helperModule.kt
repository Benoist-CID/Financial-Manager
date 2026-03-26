package fr.laforge.benoist.financialmanager.di.module

import androidx.fragment.app.FragmentActivity
import fr.laforge.benoist.financialmanager.domain.usecase.BiometricAuthenticator
import fr.laforge.benoist.financialmanager.domain.usecase.notification.NotificationHelper
import fr.laforge.benoist.financialmanager.infrastructure.auth.BiometricAuthenticatorImpl
import fr.laforge.benoist.financialmanager.infrastructure.helper.NotificationHelperImpl
import fr.laforge.benoist.financialmanager.infrastructure.service.AndroidExportService
import fr.laforge.benoist.financialmanager.infrastructure.service.NotificationListenerHelper
import fr.laforge.benoist.financialmanager.presentation.util.ExportService
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val helperModule by lazy {
    module {
        // BiometricPrompt requires a live FragmentActivity — must be a factory (never singleton)
        // so each activity instance receives its own authenticator with a fresh reference.
        factory<BiometricAuthenticator> { (activity: FragmentActivity) ->
            BiometricAuthenticatorImpl(activity = activity)
        }

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