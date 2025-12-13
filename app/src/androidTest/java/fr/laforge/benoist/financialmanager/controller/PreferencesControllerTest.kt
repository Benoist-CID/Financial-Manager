package fr.laforge.benoist.financialmanager.controller

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.laforge.benoist.financialmanager.data.repository.PreferencesRepositoryImpl
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.preferences.DataStorePreferencesInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesControllerTest {
    private lateinit var preferencesRepository: PreferencesRepository

    @Before
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        preferencesRepository = PreferencesRepositoryImpl(DataStorePreferencesInteractor(context))
    }

    @Test
    fun targetTest() {

        runBlocking {
            preferencesRepository.setSavingsTarget(12.52F)
            preferencesRepository.getSavingTarget().first().shouldBeEqualTo(12.52F)
        }
    }
}
