package com.israelaguilar.treeservicesdb.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.databinding.TreeServiceElementBinding

class TreeServiceAdapter(
    private val onTreeServiceClicked: (TreeServiceEntity) -> Unit
):  RecyclerView.Adapter<TreeServiceViewHolder>() {

    private var treeServices: MutableList<TreeServiceEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeServiceViewHolder {
        val binding =
            TreeServiceElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TreeServiceViewHolder(binding)
    }

    override fun getItemCount(): Int = treeServices.size

    override fun onBindViewHolder(holder: TreeServiceViewHolder, position: Int) {


        val treeService = treeServices[position]

        holder.bind(treeService)

        // Para manejar el click al elemento
        holder.itemView.setOnClickListener {
            onTreeServiceClicked(treeService)
        }
    }

    fun updateList(list: MutableList<TreeServiceEntity>) {
        treeServices = list
        notifyDataSetChanged()

        // Para optimizar la parte del adapter
        //DiffUtil
    }

}