package com.project.form_emulator.Room

import android.content.Context
import androidx.room.Room

class DBHelper(context: Context) {

    private val base=Room.databaseBuilder(context,DBClass::class.java,"modelBase").build()

    fun insertModel(model: PhotoModel){
        base.getDao().insertModel(model)
    }

    fun getModelByName(name: String?):PhotoModel {
       return base.getDao().getModel(name!!)
    }

    fun isModelExists(name: String?):Boolean{
        return base.getDao().isModelExists(name!!)
    }

    companion object{
       private var helper:DBHelper? = null
        fun getHelper(context: Context):DBHelper{
            if (helper==null){
                helper= DBHelper(context)
            }
            return helper as DBHelper
        }
    }
}