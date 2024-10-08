package com.israelaguilar.treeservicesdb.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.israelaguilar.treeservicesdb.R
import com.israelaguilar.treeservicesdb.application.TreeServicesDBApp
import com.israelaguilar.treeservicesdb.data.TreeServiceRepository
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object{
        const val DIALOG_ADD_TAG = "dialog1"
        const val DIALOG_EDIT_TAG = "dialog2"
    }

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
                updateUI()
            }, message = { text ->
                message(text)
            })

            dialog.show(supportFragmentManager, DIALOG_EDIT_TAG)
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
            message(text)
        })

        dialog.show(supportFragmentManager, DIALOG_ADD_TAG)
    }

    private fun message(text: String){
        Snackbar.make(
            binding.cl,
            text,
            Snackbar.LENGTH_SHORT
        )
            .setTextColor(getColor(R.color.white))
            .setBackgroundTint(getColor(R.color.snackbar))
            .show()
    }

    private fun updateUI(){
        lifecycleScope.launch {
            treeServices = repository.getAllTreeServices()
            binding.tvSinRegistros.visibility =
                if(treeServices.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            treeServiceAdapter.updateList(treeServices)
        }
    }
}