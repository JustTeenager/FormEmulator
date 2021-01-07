package com.project.form_emulator.Room

import android.content.Context
import android.util.Log
import androidx.room.Room

class DBHelper(context: Context) {

    private val base=Room.databaseBuilder(context,DBClass::class.java,"modelBase").build()

    fun insertModel(model: PhotoModel){
        Log.d("InsertDB:",Thread.currentThread().name)
        base.getDao().insertModel(model)
    }

    fun getModelByName(name: String?):PhotoModel {
        Log.d("GetDB:",Thread.currentThread().name)
       return base.getDao().getModel(name!!)
    }

    fun isModelExists(name: String?):Boolean{
        Log.d("CheckDB:",Thread.currentThread().name)
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