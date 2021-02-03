package com.note.notenest.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import com.note.notenest.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class NoteViewModel @ViewModelInject constructor(val myRepo: NoteRepository) : ViewModel() {


    private val _noteList = MutableLiveData<List<NoteModel>>()
    val noteListLiveData: LiveData<List<NoteModel>> = _noteList


    fun addNoteItem(noteModel: NoteModel) = viewModelScope.launch {
        myRepo.insertNoteItem(noteModel)
    }

    fun getNoteList() = myRepo.getNoteDatabase

    fun getNoteListSortedByColor() = myRepo.sortNotesByColor
    fun getNoteListSortedByCreation() = myRepo.sortNotesByCreation
    fun getNoteListSortedByTitle() = myRepo.sortNotesByTitle
    fun deleteAllNotesItem() = viewModelScope.launch {
        myRepo.deleteNoteDatabase()
    }

    fun searchDatabase(query: String): LiveData<List<NoteModel>> {
        if (query != "")
            return myRepo.searchNoteDatabase("%" + query + "%")
        else
            return myRepo.getNoteDatabase
    }


    fun addTrashItem(trashModel: TrashModel) = viewModelScope.launch {
        myRepo.insertTrashItem(trashModel)
    }


}