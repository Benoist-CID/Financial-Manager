package fr.laforge.benoist.preferences

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataStorePreferencesTest {
    @Test
    fun floatTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val ds = DataStorePreferencesInteractor(context)

        runBlocking {
            ds.setFloat("TEST", 1.0F)
            ds.getFloat("TEST").first().shouldBeEqualTo(1.0F)
        }
    }
}
