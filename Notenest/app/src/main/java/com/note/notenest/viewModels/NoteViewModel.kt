package com.note.notenest.viewModels

import android.content.Context
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.snackbar.Snackbar
import com.note.notenest.R
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import com.note.notenest.data.repository.NoteRepository
import com.note.notenest.utils.Constants
import com.note.notenest.utils.Constants.ARCHIVE_TO_NOTE
import com.note.notenest.utils.Constants.ARCHIVE_TO_TRASH
import com.note.notenest.utils.Constants.NOTE_EMPTY
import com.note.notenest.utils.Constants.NOTE_TO_ARCHIVE
import com.note.notenest.utils.Constants.NOTE_TO_TRASH
import com.note.notenest.utils.Constants.TRASH_TO_NOTE
import com.note.notenest.utils.Constants.TRASH_TO_NOTE_EDIT
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

    private fun deleteNoteItem(noteModel: NoteModel) = viewModelScope.launch {
        myRepo.deleteNoteItem(noteModel)
    }

    fun searchDatabase(query: String): LiveData<List<NoteModel>> {
        if (query != "")
            return myRepo.searchNoteDatabase("%" + query + "%")
        else
            return myRepo.getNoteDatabase
    }


    private fun addArchiveItem(archiveItem: ArchiveModel) = viewModelScope.launch {
        myRepo.insertArchiveItem(archiveItem)
    }

    private fun deleteArchiveItem(archiveItem: ArchiveModel) = viewModelScope.launch {
        myRepo.deleteArchiveItem(archiveItem)
    }

    private fun deleteArchiveDatabase() = viewModelScope.launch {
        myRepo.deleteArchiveDatabase()
    }


    private fun addTrashItem(trashItem: TrashModel) = viewModelScope.launch {
        myRepo.insertTrashItem(trashItem)
    }

    private fun deleteTrashItem(trashItem: TrashModel) = viewModelScope.launch {
        myRepo.deleteTrashItem(trashItem)
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

    fun moveItem(
        from: String,
        noteItem: NoteModel,
        archiveItem: ArchiveModel,
        trashItem: TrashModel,
        view: View
    ) {


        lateinit var message: String

        // Set message.
        if (from == NOTE_TO_ARCHIVE) {
            message = "Note moved to archive"
        } else if (from == NOTE_TO_TRASH || from == ARCHIVE_TO_TRASH) {
            message = "Note moved to trash"
        } else if (from == ARCHIVE_TO_NOTE) {
            message = "Note moved to home"
        } else if (from == TRASH_TO_NOTE) {
            message = "Note moved to home"
        } else if (from == TRASH_TO_NOTE_EDIT) {
            message = "Cannot edit this file in trash can"
        }

        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

        when (from) {
            NOTE_TO_ARCHIVE -> {
                // Insert/delete data.
                deleteNoteItem(noteItem)
                addArchiveItem(archiveItem)

                // Show SnackBar to undo.
                snackBar.setAction("Undo") {
                    // Insert/delete data.
                    addNoteItem(noteItem)
                    deleteArchiveItem(archiveItem)
                }
            }
            NOTE_TO_TRASH -> {
                deleteNoteItem(noteItem)
                addTrashItem(trashItem)

                snackBar.setAction("Undo") {
                    addNoteItem(noteItem)
                    deleteTrashItem(trashItem)
                }
            }
            ARCHIVE_TO_NOTE -> {
                deleteArchiveItem(archiveItem)
                addNoteItem(noteItem)

                snackBar.setAction("Undo") {
                    addArchiveItem(archiveItem)
                    deleteNoteItem(noteItem)
                }.show()

                // Navigate back.
                view.findNavController().navigateUp()
                //      .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            ARCHIVE_TO_TRASH -> {
                deleteArchiveItem(archiveItem)
                addTrashItem(trashItem)

                snackBar.setAction("Undo") {
                    addArchiveItem(archiveItem)
                    deleteTrashItem(trashItem)
                }

                view.findNavController().navigateUp()
                //      .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            TRASH_TO_NOTE -> {
                deleteTrashItem(trashItem)
                addNoteItem(noteItem)

                snackBar.setAction("Undo") {
                    addTrashItem(trashItem)
                    deleteNoteItem(noteItem)
                }

                view.findNavController().navigateUp()
                //.navigate(R.id.action_trashUpdateFragment_to_trashFragment)
            }

            TRASH_TO_NOTE_EDIT -> {
                snackBar.setAction("Restore") {
                    deleteTrashItem(trashItem)
                    addNoteItem(noteItem)

                    Snackbar.make(
                        view,
                        "Moved to notes",
                        Snackbar.LENGTH_LONG
                    ).setAction("Undo") {
                        addTrashItem(trashItem)
                        deleteNoteItem(noteItem)
                    }.show()

                    view.findNavController().navigateUp()
                    //     .navigate(R.id.action_trashUpdateFragment_to_trashFragment)
                }
            }
        }

//        snackBar.setActionTextColor(
//            ActivityCompat.getColor(getApplication(), R.color.snackBarActionColor)
//        ).show()

        snackBar.show()

    }


}