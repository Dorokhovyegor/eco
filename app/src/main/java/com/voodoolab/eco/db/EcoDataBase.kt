package com.voodoolab.eco.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.voodoolab.eco.dao.CityDao
import com.voodoolab.eco.entities.CityEntity

@Database(entities = arrayOf(CityEntity::class), version = 1)
abstract class EcoDataBase : RoomDatabase() {

    abstract fun citiesDao(): CityDao

    companion object{
        private var instance: EcoDataBase? = null

        @Synchronized
        fun get(context: Context): EcoDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                    EcoDataBase::class.java, "EcoDataBase")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                        }
                    }).build()
            }
            return instance!!
        }
    }

}