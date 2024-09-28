package com.israelaguilar.treeservicesdb.ui

import androidx.recyclerview.widget.RecyclerView
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.databinding.TreeServiceElementBinding

class TreeServiceViewHolder(
    private val binding: TreeServiceElementBinding
): RecyclerView.ViewHolder(binding.root){
    fun bind(treeService: TreeServiceEntity){
        binding.apply {
            tvServiceTitle.text = treeService.serviceTitle
            tvServiceDescription.text = treeService.serviceDescription
            tvPrice.text = treeService.price.toString()
            tvAvailability.text = treeService.availabilty
        }
    }

}