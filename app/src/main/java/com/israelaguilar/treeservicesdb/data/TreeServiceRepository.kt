package com.israelaguilar.treeservicesdb.data

import com.israelaguilar.treeservicesdb.data.db.TreeServiceDao
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity

class TreeServiceRepository(
    private val treeServiceDao: TreeServiceDao
) {

    suspend fun insertTreeService(treeService: TreeServiceEntity){
        treeServiceDao.insertTreeService(treeService)
    }

    suspend fun  getAllTreeServices(): MutableList<TreeServiceEntity> = treeServiceDao.getAllTreeServices()

    suspend fun updateTreeService(treeService: TreeServiceEntity){
        treeServiceDao.updateTreeService(treeService)
    }

    suspend fun deleteTreeService(treeService: TreeServiceEntity){
        treeServiceDao.deleteTreeService(treeService)
    }


}