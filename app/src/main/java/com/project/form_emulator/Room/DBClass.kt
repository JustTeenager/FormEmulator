package com.project.form_emulator.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1,entities = [PhotoModel::class])
abstract class DBClass: RoomDatabase() {
    abstract fun getDao():Dao
}