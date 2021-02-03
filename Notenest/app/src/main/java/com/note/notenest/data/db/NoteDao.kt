package com.note.notenest.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel


@Dao
interface NoteDao {

    //Notes
    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getNoteDatabase(): LiveData<List<NoteModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteItem(noteModel: NoteModel)

    @Update
    suspend fun updateNoteItem(noteModel: NoteModel)

    @Delete
    suspend fun deleteNoteItem(noteModel: NoteModel)

    @Query("DELETE FROM note_table")
    suspend fun deleteNoteDatabase()

    @Query("SELECT * FROM note_table WHERE title LIKE :searchQuery")
    fun searchNoteDatabase(searchQuery: String): LiveData<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY title ASC")
    fun sortNotesByTitle(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun sortNotesByCreation(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY color ASC")
    fun sortNotesByColor(): LiveData<List<NoteModel>>


    //archive

    @Query("SELECT * FROM archive_table ORDER BY id DESC")
    fun getArchiveDatabase(): LiveData<List<ArchiveModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArchiveItem(archiveModel: ArchiveModel)

    @Update
    suspend fun updateArchiveItem(archiveModel: ArchiveModel)

    @Delete
    suspend fun deleteArchiveItem(archiveModel: ArchiveModel)

    @Query("DELETE FROM archive_table")
    suspend fun deleteArchiveDatabase()

    @Query("SELECT * FROM archive_table WHERE title LIKE :searchQuery")
    fun searchArchiveDatabase(searchQuery: String): LiveData<List<ArchiveModel>>


    //trash
    @Query("SELECT * FROM trash_table ORDER BY id DESC")
    fun getTrashDatabase(): LiveData<List<TrashModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrashItem(trashModel: TrashModel)

    @Delete
    suspend fun deleteTrashItem(trashModel: TrashModel)

    @Query("DELETE FROM trash_table")
    suspend fun deleteTrashDatabase()

}