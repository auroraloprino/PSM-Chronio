package com.unibo.android.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unibo.android.data.local.dao.BoardDao
import com.unibo.android.data.local.dao.BoardTagDao
import com.unibo.android.data.local.dao.CardDao
import com.unibo.android.data.local.dao.ColumnDao
import com.unibo.android.data.local.dao.EventDao
import com.unibo.android.data.local.dao.TagDao
import com.unibo.android.data.local.entity.BoardEntity
import com.unibo.android.data.local.entity.BoardTagEntity
import com.unibo.android.data.local.entity.CardEntity
import com.unibo.android.data.local.entity.CardTagCrossRef
import com.unibo.android.data.local.entity.ColumnEntity
import com.unibo.android.data.local.entity.EventEntity
import com.unibo.android.data.local.entity.EventTagCrossRef
import com.unibo.android.data.local.entity.TagEntity

@Database(
    entities = [
        EventEntity::class,
        TagEntity::class,
        EventTagCrossRef::class,
        BoardEntity::class,
        ColumnEntity::class,
        CardEntity::class,
        BoardTagEntity::class,
        CardTagCrossRef::class
    ],
    version = 4
)
abstract class ChronioDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun tagDao(): TagDao
    abstract fun boardDao(): BoardDao
    abstract fun columnDao(): ColumnDao
    abstract fun cardDao(): CardDao
    abstract fun boardTagDao(): BoardTagDao

    companion object {
        @Volatile
        private var INSTANCE: ChronioDatabase? = null

        fun getInstance(context: Context): ChronioDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
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
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
