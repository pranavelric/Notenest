package com.note.notenest.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.note.notenest.R
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import com.note.notenest.data.repository.NoteRepository
import com.note.notenest.utils.Constants
import com.note.notenest.utils.Constants.NOTE_EMPTY
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
    private fun deleteAllNotesItem() = viewModelScope.launch {
        myRepo.deleteNoteDatabase()
    }

    fun searchDatabase(query: String): LiveData<List<NoteModel>> {
        if (query != "")
            return myRepo.searchNoteDatabase("%" + query + "%")
        else
            return myRepo.getNoteDatabase
    }


    private fun deleteArchiveDatabase() = viewModelScope.launch {
        myRepo.deleteArchiveDatabase()
    }

    private fun deleteTrashDatabase() = viewModelScope.launch {
        myRepo.deleteTrashDatabase()
    }


    fun emptyDatabase(context: Context, database: String) {

        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))

        when (database) {
            Constants.TRASH_EMPTY -> {
                dialog.show {
                    icon(R.drawable.ic_delete_all)
                    title(R.string.dialog_empty_trash)
                    message(R.string.dialog_delete_confirmation_trash)
                    positiveButton(R.string.dialog_empty_trash) {
                        deleteTrashDatabase()
                    }
                    negativeButton(R.string.dialog_negative)
                }
            }
            else -> {
                dialog.show {
                    icon(R.drawable.ic_delete_all)
                    title(R.string.dialog_delete_all)
                    message(R.string.dialog_delete_confirmation)
                    positiveButton(R.string.dialog_confirmation) {
                        when (database) {
                            // Delete database.
                            Constants.NOTE_EMPTY -> deleteAllNotesItem()
                            Constants.ARCHIVE_EMPTY -> deleteArchiveDatabase()
                        }

                    }
                    negativeButton(R.string.dialog_negative)
                }
            }
        }
    }


}