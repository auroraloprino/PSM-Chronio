package com.unibo.android.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unibo.android.data.local.dao.BoardDao
import com.unibo.android.data.local.dao.CardDao
import com.unibo.android.data.local.dao.ColumnDao
import com.unibo.android.data.local.entity.BoardEntity
import com.unibo.android.data.local.entity.CardEntity
import com.unibo.android.data.local.entity.CardTagCrossRef
import com.unibo.android.data.local.entity.ColumnEntity

@Database(
    entities = [
        BoardEntity::class,
        ColumnEntity::class,
        CardEntity::class,
        CardTagCrossRef::class
    ],
    version = 1
)
abstract class ChronioDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun columnDao(): ColumnDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: ChronioDatabase? = null

        fun getInstance(context: Context): ChronioDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChronioDatabase::class.java,
                    "chronio_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}