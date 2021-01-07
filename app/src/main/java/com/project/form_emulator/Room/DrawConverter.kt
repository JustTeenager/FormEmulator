package com.project.form_emulator.Room

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.room.TypeConverter


class DrawConverter {

    @TypeConverter
    fun fromDrawable(dr: Drawable): String {
        return dr.toString()
    }

    @TypeConverter
    fun toDrawable(string: String): Drawable {
        return ColorDrawable()
    }
}