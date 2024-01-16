package fr.laforge.benoist.financialmanager.controller

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.laforge.benoist.preferences.DataStorePreferencesInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesControllerTest {
    private lateinit var preferencesController: PreferencesController

    @Before
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        preferencesController = PreferencesControllerImpl(DataStorePreferencesInteractor(context))
    }

    @Test
    fun targetTest() {

        runBlocking {
            preferencesController.setSavingsTarget(12.52F)
            preferencesController.getSavingTarget().first().shouldBeEqualTo(12.52F)
        }
    }
}
