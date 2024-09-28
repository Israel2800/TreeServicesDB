package com.israelaguilar.treeservicesdb.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.util.Constants

@Database(
    entities = [TreeServiceEntity::class],
    version = 1,
    exportSchema = true
)

abstract class TreeServiceDatabase: RoomDatabase(){
    abstract fun treeServiceDao(): TreeServiceDao

    companion object{
        @Volatile
        private var INSTANCE: TreeServiceDatabase? = null

        fun getDataBase(context: Context): TreeServiceDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TreeServiceDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
