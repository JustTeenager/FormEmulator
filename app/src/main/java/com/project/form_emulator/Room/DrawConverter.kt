package com.project.form_emulator.Room

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.room.TypeConverter
import java.util.*


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