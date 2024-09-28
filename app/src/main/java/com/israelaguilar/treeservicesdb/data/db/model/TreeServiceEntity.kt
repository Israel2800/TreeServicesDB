package com.israelaguilar.treeservicesdb.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.israelaguilar.treeservicesdb.util.Constants

@Entity(tableName = Constants.DATABASE_NAME)
data class TreeServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tree_service_id")
    var id: Long = 0,
    @ColumnInfo(name = "tree_service_title")
    var serviceTitle: String,
    @ColumnInfo(name = "tree_service_description")
    var serviceDescription: String,
    @ColumnInfo(name = "tree_service_price")
    var price: Int,
    @ColumnInfo(name = "tree_service_availability")
    var availabilty: String
)