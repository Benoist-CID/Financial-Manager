package fr.laforge.benoist.repository.implementations.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import fr.laforge.benoist.repository.implementations.room.database.AppDatabase

open class BaseRoomTest {
    protected lateinit var db: AppDatabase

    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
    }
}