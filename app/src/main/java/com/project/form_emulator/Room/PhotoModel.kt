package com.project.form_emulator.Room

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters


@Entity(tableName = "models")
@TypeConverters(DrawConverter::class)
data class PhotoModel(@PrimaryKey val name: String, @ColumnInfo(name = "image") var photoDrawable:Drawable, @ColumnInfo(name = "like") val like:Int, @ColumnInfo(name = "group") val group:Int)