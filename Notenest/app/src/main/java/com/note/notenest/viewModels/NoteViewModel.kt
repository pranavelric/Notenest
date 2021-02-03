package com.note.notenest.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class NoteViewModel @ViewModelInject constructor(val myRepo: NoteRepository) : ViewModel() {


    


    fun addNoteItem(noteModel: NoteModel) = viewModelScope.launch {
        myRepo.insertNoteItem(noteModel)
    }






}