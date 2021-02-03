package com.note.notenest.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "trash_table")
@Parcelize
data class TrashModel (
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var title: String,
    var description: String,
    var color: Int,
    var sketches: Int?,
    var files: String?,
    val lastModified:String


):Parcelable