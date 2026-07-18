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
                    },
                    androidx.room.migration.Migration(3, 4) { db ->
                        db.execSQL("CREATE TABLE IF NOT EXISTS `boards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL DEFAULT '', `coverImageUrl` TEXT, `createdAt` INTEGER NOT NULL DEFAULT 0)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_board_columns_boardId` ON `board_columns` (`boardId`)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `board_columns` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `position` INTEGER NOT NULL, `boardId` INTEGER NOT NULL, FOREIGN KEY(`boardId`) REFERENCES `boards`(`id`) ON DELETE CASCADE)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_cards_columnId` ON `cards` (`columnId`)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL DEFAULT '', `columnId` INTEGER NOT NULL, `position` INTEGER NOT NULL, `isDone` INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(`columnId`) REFERENCES `board_columns`(`id`) ON DELETE CASCADE)")
                        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_board_tags_name` ON `board_tags` (`name`)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `board_tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `color` TEXT NOT NULL DEFAULT '#6200EE')")
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_card_tag_cross_ref_cardId` ON `card_tag_cross_ref` (`cardId`)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_card_tag_cross_ref_tagId` ON `card_tag_cross_ref` (`tagId`)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `card_tag_cross_ref` (`cardId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`cardId`, `tagId`), FOREIGN KEY(`cardId`) REFERENCES `cards`(`id`) ON DELETE CASCADE, FOREIGN KEY(`tagId`) REFERENCES `board_tags`(`id`) ON DELETE CASCADE)")
                    }
                )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
