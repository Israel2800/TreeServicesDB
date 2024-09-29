package com.israelaguilar.treeservicesdb.ui

import androidx.recyclerview.widget.RecyclerView
import com.israelaguilar.treeservicesdb.R
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.databinding.TreeServiceElementBinding

class TreeServiceViewHolder(
    private val binding: TreeServiceElementBinding
): RecyclerView.ViewHolder(binding.root){
    fun bind(treeService: TreeServiceEntity){
        binding.apply {
            tvServiceTitle.text = treeService.serviceTitle
            tvServiceDescription.text = treeService.serviceDescription
            tvPrice.text = treeService.price
            tvDuration.text = treeService.duration

            val context = binding.root.context
            val spinnerItems = context.resources.getStringArray(R.array.service_titles)
            val imageResourse =
                when (treeService.serviceTitle) {
                    spinnerItems[1] -> R.drawable.treetrimming
                    spinnerItems[2] -> R.drawable.treeremoval
                    spinnerItems[3] -> R.drawable.landclearing
                    spinnerItems[4] -> R.drawable.stumpgrinding
                    spinnerItems[5] -> R.drawable.treeplanting
                    else -> R.drawable.treeplanting
                }
            ivIcon.setImageResource(imageResourse)



            /*when(treeService.image){
                1 -> {
                    ivIcon.setImageResource(R.drawable.treetrimming)
                }
                2 -> {
                    ivIcon.setImageResource(R.drawable.treetrimming)
                }
                3 -> {
                    ivIcon.setImageResource(R.drawable.treetrimming)
                }
                4 -> {
                    ivIcon.setImageResource(R.drawable.treetrimming)
                }
            }*/


        }
    }

}