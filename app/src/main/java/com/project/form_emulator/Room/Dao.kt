package com.project.form_emulator.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface Dao {

    @Insert
    fun insertModel(model: PhotoModel)

    @Query("SELECT * FROM models WHERE name = :modelName")
    fun getModel(modelName: String):PhotoModel

    @Query("SELECT EXISTS (SELECT 1 FROM models WHERE name = :modelName)")
    fun isModelExists(modelName: String): Boolean
}