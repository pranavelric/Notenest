package com.note.notenest.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MySharedPrefrences @Inject constructor(@ApplicationContext context: Context) {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.SHARED_PREF, 0)
    }

    private var editor = sp.edit()


    fun cleaSession() {
        editor.clear()
        editor.commit()
    }


    fun setNotesLayout(value: Boolean) {
        editor.putBoolean(Constants.NOTE_LAYOUT, value)
        editor.commit()
    }

    fun getNotesLayout(): Boolean {
        return sp.getBoolean(Constants.NOTE_LAYOUT, false)
    }


    fun setArchiveLayout(value: Boolean) {
        editor.putBoolean(Constants.ARCHIVE_LAYOUT, value)
        editor.commit()
    }

    fun getArchiveLayout(): Boolean {
        return sp.getBoolean(Constants.ARCHIVE_LAYOUT, false)
    }

}