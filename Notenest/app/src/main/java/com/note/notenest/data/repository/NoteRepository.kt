package com.note.notenest.data.repository

import androidx.lifecycle.LiveData
import com.note.notenest.data.db.NoteDao
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NoteRepository @Inject constructor(val myDao: NoteDao) {

    //Notes
    val getNoteDatabase: LiveData<List<NoteModel>> = myDao.getNoteDatabase()

    val sortNotesByTitle: LiveData<List<NoteModel>> = myDao.sortNotesByTitle()
    val sortNotesByCreation: LiveData<List<NoteModel>> = myDao.sortNotesByCreation()
    val sortNotesByColor: LiveData<List<NoteModel>> = myDao.sortNotesByColor()

    suspend fun insertNoteItem(noteModel: NoteModel) {
        myDao.insertNoteItem(noteModel)
    }

    suspend fun updateNoteItem(noteModel: NoteModel) {
        myDao.updateNoteItem(noteModel)
    }

    suspend fun deleteNoteItem(noteModel: NoteModel) {
        myDao.deleteNoteItem(noteModel)
    }

    suspend fun deleteNoteDatabase() {
        myDao.deleteNoteDatabase()
    }

    fun searchNoteDatabase(searchQuery: String): LiveData<List<NoteModel>> {
        return myDao.searchNoteDatabase(searchQuery)
    }

    //archive
    val getArchiveDatabase: LiveData<List<ArchiveModel>> = myDao.getArchiveDatabase()


    suspend fun insertArchiveItem(archiveModel: ArchiveModel) {
        myDao.insertArchiveItem(archiveModel)
    }

    suspend fun updateArchiveItem(archiveModel: ArchiveModel) {
        myDao.updateArchiveItem(archiveModel)
    }

    suspend fun deleteArchiveItem(archiveModel: ArchiveModel) {
        myDao.deleteArchiveItem(archiveModel)
    }

    suspend fun deleteArchiveDatabase() {
        myDao.deleteArchiveDatabase()
    }

    fun searchArchiveDatabase(searchQuery: String): LiveData<List<ArchiveModel>> {
        return myDao.searchArchiveDatabase(searchQuery)
    }

    //trash

    val getTrashDatabase: LiveData<List<TrashModel>> = myDao.getTrashDatabase()

    suspend fun insertTrashItem(trashModel: TrashModel) {
        myDao.insertTrashItem(trashModel)
    }

    suspend fun deleteTrashItem(trashModel: TrashModel) {
        myDao.deleteTrashItem(trashModel)
    }

    suspend fun deleteTrashDatabase() {
        myDao.deleteTrashDatabase()
    }


}