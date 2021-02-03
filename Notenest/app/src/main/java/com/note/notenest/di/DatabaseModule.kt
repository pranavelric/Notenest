package com.note.notenest.di

import android.content.Context

import androidx.room.Room
import com.note.notenest.data.db.NoteDao
import com.note.notenest.data.db.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context, NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providesService(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }


}
