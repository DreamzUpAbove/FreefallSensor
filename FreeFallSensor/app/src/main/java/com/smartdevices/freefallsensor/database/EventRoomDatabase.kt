package com.smartdevices.freefallsensor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartdevices.freefallsensor.models.Event
import kotlinx.coroutines.CoroutineScope

/**
 * EventRoomDatabase
 * Localstorage for all the recorded events
 */
@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventRoomDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventRoomDatabase? = null

        /**
         * getDatabase
         *
         * getInstance of EventRoomDatabase
         */
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): EventRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventRoomDatabase::class.java,
                    "event_database"
                )
                    .addCallback(EventDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        /**
         * EventDatabaseCallback
         *
         * callback of EventDatabaseCallback
         */
        private class EventDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }
    }
}
