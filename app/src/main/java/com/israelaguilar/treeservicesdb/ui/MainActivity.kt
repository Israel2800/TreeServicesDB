package com.israelaguilar.treeservicesdb.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.israelaguilar.treeservicesdb.R
import com.israelaguilar.treeservicesdb.application.TreeServicesDBApp
import com.israelaguilar.treeservicesdb.data.TreeServiceRepository
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var treeServices: MutableList<TreeServiceEntity> = mutableListOf()
    private lateinit var repository: TreeServiceRepository

    private lateinit var treeServiceAdapter: TreeServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        repository = (application as TreeServicesDBApp).repository

        treeServiceAdapter = TreeServiceAdapter{ selectedTreeService ->

            val dialog = TreeServiceDialog(newTreeService = false, treeService = selectedTreeService, updateUI = {
                upateUI()
            }, message = { text ->
                message(text)
            })

            dialog.show(supportFragmentManager, "dialog2")
        }

        binding.rvTreeServices.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = treeServiceAdapter
        }

        updateUI()

    }

    fun click(view: View) {
        val dialog = TreeServiceDialog(updateUI = {
            updateUI()
        }, message = { text ->
            messgae(text)
        })

        dialog.show(supportFragmentManager, dialog1)
    }
}