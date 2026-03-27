package fr.laforge.benoist.financialmanager.infrastructure.service

import android.content.Context

/**
 * Provides the application version name as declared in the build configuration.
 *
 * Encapsulates the [android.content.pm.PackageManager] call so that the
 * presentation layer never depends on Android system APIs directly.
 *
 * @param context Application context used to query [android.content.pm.PackageManager].
 */
class AppVersionProvider(private val context: Context) {

    /**
     * Returns the version name of the application (e.g. `"1.0"`).
     *
     * @return The version name string, or `"-"` if it cannot be determined.
     */
    fun getVersionName(): String =
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: "-"
}
