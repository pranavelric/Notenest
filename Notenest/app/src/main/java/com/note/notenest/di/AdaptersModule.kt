package com.note.notenest.di

import com.note.notenest.adapters.ArchiveAdapter
import com.note.notenest.adapters.NoteAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent


@InstallIn(FragmentComponent::class)
@Module
class AdaptersModule {

    @Provides
    fun providesNoteAdapter(): NoteAdapter {
        return NoteAdapter()
    }

    @Provides
    fun providesArchiveNoteAdapter(): ArchiveAdapter {
        return ArchiveAdapter()
    }


}