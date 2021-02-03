package com.note.notenest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel


@Database(
    entities = [NoteModel::class, ArchiveModel::class, TrashModel::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        val DATABASE_NAME = "MY_DATABASE"

    }


}