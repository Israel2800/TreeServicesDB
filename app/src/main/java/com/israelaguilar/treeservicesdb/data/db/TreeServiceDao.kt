package com.israelaguilar.treeservicesdb.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.util.Constants

@Dao
interface TreeServiceDao {

    // Funciones para interactuar con la base de datos

    //CRUD
    //Create
    @Insert
    suspend fun insertTreeService(treeService: TreeServiceEntity)

    @Insert
    suspend fun insertTreeServices(treeService: List<TreeServiceEntity>)

    // Read
    @Query("SELECT * FROM ${Constants.DATABASE_TREE_SERVICE_TABLE}")
    suspend fun getAllTreeServices(): MutableList<TreeServiceEntity>

    // Update
    @Update
    suspend fun updateTreeService(treeService: TreeServiceEntity)

    // Delete
    @Delete
    suspend fun deleteTreeService(treeService: TreeServiceEntity)
}