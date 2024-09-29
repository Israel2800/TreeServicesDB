package com.israelaguilar.treeservicesdb.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.israelaguilar.treeservicesdb.R
import com.israelaguilar.treeservicesdb.application.TreeServicesDBApp
import com.israelaguilar.treeservicesdb.data.TreeServiceRepository
import com.israelaguilar.treeservicesdb.data.db.model.TreeServiceEntity
import com.israelaguilar.treeservicesdb.databinding.TreeServiceDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class TreeServiceDialog(
    private val newTreeService: Boolean = true,
    private val treeService: TreeServiceEntity = TreeServiceEntity(
        serviceTitle = "",
        serviceDescription = "",
        price = "",
        duration = ""
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
) : DialogFragment() {

    private var _binding: TreeServiceDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog
    private var saveButton: Button? = null
    private lateinit var repository: TreeServiceRepository

    // Configuración inicial del dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = TreeServiceDialogBinding.inflate(requireActivity().layoutInflater)

        repository = (requireContext().applicationContext as TreeServicesDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        // Establecer los valores iniciales en los campos de texto
        // Configurar el AutoCompleteTextView con los datos del array
        val serviceTitles = resources.getStringArray(R.array.service_titles).toMutableList()

        // Crea un ArrayAdapter personalizado
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            serviceTitles
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                // Si la posición es 0, oculta el elemento del spinner
                if (position == 0) {
                    view.visibility = View.INVISIBLE
                    view.layoutParams = AbsListView.LayoutParams(0, 0)
                } else {
                    view.visibility = View.VISIBLE
                    view.layoutParams = AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                return view
            }
        }.also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerServiceTitle.adapter = adapter


        // Establece el valor del spinner basado en el servicio existente
        if (treeService.serviceTitle.isNotEmpty()) {
            val defaultIndex = serviceTitles.indexOf(treeService.serviceTitle)
            if (defaultIndex != -1) {
                binding.spinnerServiceTitle.setSelection(defaultIndex)
            }
        }


        binding.apply {
            // Configura otros campos de texto
            tietServiceDescription.setText(treeService.serviceDescription)
            tietPrice.setText(treeService.price)
            tietDuration.setText(treeService.duration)

            // Asigna el adaptador al spinner
            //spinnerServiceTitle.setAdapter(adapter)

        }


        dialog = if (newTreeService)
            buildDialog(getString(R.string.save), getString(R.string.cancel), {
                // Acción de guardar
                binding.apply {
                    treeService.apply {
                        serviceTitle = spinnerServiceTitle.selectedItem.toString()
                        serviceDescription = tietServiceDescription.text.toString()
                        price = getString(R.string.currency_symbol) + tietPrice.text.toString()
                        duration = tietDuration.text.toString()
                    }
                }

                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val result = async {
                            repository.insertTreeService(treeService)
                        }

                        result.await()

                        withContext(Dispatchers.Main) {
                            message(getString(R.string.save_message))
                            updateUI()

                        }
                    }
                } catch (e: IOException) {
                    message(getString(R.string.save_error_message))
                }
            }, {
                // Acción de cancelar
            })
        else
            buildDialog(getString(R.string.update), getString(R.string.delete), {
                // Acción de actualizar
                binding.apply {
                    treeService.apply {
                        serviceTitle = spinnerServiceTitle.selectedItem.toString()
                        serviceDescription = tietServiceDescription.text.toString()
                        price = tietPrice.text.toString()
                        duration = tietDuration.text.toString()
                    }
                }

                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val result = async {
                            repository.updateTreeService(treeService)
                        }
                        result.await()

                        withContext(Dispatchers.Main) {
                            message(getString(R.string.update_message))
                            updateUI()
                        }
                    }
                } catch (e: IOException) {
                    message(getString(R.string.service_update_error_message))
                }

            }, {
                // Acción de borrar
                val context = requireContext()

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_message, treeService.serviceTitle))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        try {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val result = async {
                                    repository.deleteTreeService(treeService)
                                }
                                result.await()

                                withContext(Dispatchers.Main) {
                                    message(context.getString(R.string.delete_message))
                                    updateUI()
                                }
                            }
                        } catch (e: IOException) {
                            message(context.getString(R.string.delete_error_message))
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            })




        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val alertDialog = dialog as AlertDialog
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.apply {
            setupTextWatcher(
                spinnerServiceTitle,
                tietServiceDescription,
                tietPrice,
                tietDuration
            )
        }
    }

    private fun validateFields(): Boolean {

        val isSpinnerItemSelected = binding.spinnerServiceTitle.selectedItemPosition != 0

        return isSpinnerItemSelected &&
                binding.tietServiceDescription.text.toString().isNotEmpty() &&
                binding.tietPrice.text.toString().isNotEmpty() &&
                binding.tietDuration.text.toString().isNotEmpty()
    }


    private fun setupTextWatcher(vararg textFields: View) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        }

        textFields.forEach { textField ->
            if (textField is TextInputEditText) {
                textField.addTextChangedListener(textWatcher)
            } else if (textField is AutoCompleteTextView) {
                textField.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        saveButton?.isEnabled = validateFields()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        }
    }

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle(R.string.treeService)
            .setPositiveButton(btn1Text) { _, _ ->
                positiveButton()
            }.setNegativeButton(btn2Text) { _, _ ->
                negativeButton()
            }
            .create()
}
