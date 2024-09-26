package com.israelaguilar.treeservicesdb.application

import android.app.Application
import com.israelaguilar.treeservicesdb.data.TreeServiceRepository
import com.israelaguilar.treeservicesdb.data.db.TreeServiceDatabase

class TreeServicesDBApp: Application() {

    private val database by lazy {
        TreeServiceDatabase.getDataBase(this@TreeServicesDBApp)
    }

    val repository by lazy {
        TreeServiceRepository(database.treeServiceDao())
    }
}