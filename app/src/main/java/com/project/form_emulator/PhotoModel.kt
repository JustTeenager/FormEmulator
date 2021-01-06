package com.project.form_emulator

import android.content.res.AssetManager
import android.graphics.drawable.Drawable

class PhotoModel(private val path: String,private val manager:AssetManager){

    val photoDrawable:Drawable

    init {
        photoDrawable = getDrawable()
    }

    private fun getDrawable():Drawable{
        return Drawable.createFromStream(manager.open("images/$path"),null)
    }

    fun getName():String{
        return path.replace(".jpg", "")
    }
}