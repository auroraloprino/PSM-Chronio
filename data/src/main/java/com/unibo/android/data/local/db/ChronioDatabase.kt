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
    version = 1
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
                ).build().also { INSTANCE = it }
            }
    }
}
