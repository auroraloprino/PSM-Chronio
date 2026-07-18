package com.unibo.android.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unibo.android.data.local.dao.EventDao
import com.unibo.android.data.local.dao.TagDao
import com.unibo.android.data.local.entity.EventEntity
import com.unibo.android.data.local.entity.EventTagCrossRef
import com.unibo.android.data.local.entity.TagEntity

@Database(
    entities = [
        EventEntity::class,
        TagEntity::class,
        EventTagCrossRef::class
    ],
    version = 3
)
abstract class ChronioDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun tagDao(): TagDao

    companion object {
        @Volatile
        private var INSTANCE: ChronioDatabase? = null

        fun getInstance(context: Context): ChronioDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ChronioDatabase::class.java,
                    "chronio_database"
                ).addMigrations(
                    androidx.room.migration.Migration(1, 2) { db ->
                        db.execSQL("ALTER TABLE events ADD COLUMN allDay INTEGER NOT NULL DEFAULT 0")
                        db.execSQL("UPDATE events SET allDay = 1 WHERE (endTime - startTime) >= 86400000")
                    },
                    androidx.room.migration.Migration(2, 3) { db ->
                        db.execSQL("ALTER TABLE tags ADD COLUMN isSystem INTEGER NOT NULL DEFAULT 0")
                    }
                ).build().also { INSTANCE = it }
            }
    }
}
