package com.israelaguilar.treeservicesdb.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
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
        // El title se convertirá en un dropdown list
        serviceTitle = "",
        serviceDescription = "",
        price = 0,
        availabilty = ""
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
): DialogFragment() {

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

        // Se establecen en los text input edit text los valores del objeto tree service

        binding.apply {
            tietServiceTitle.setText(treeService.serviceTitle)
            tietServiceDescription.setText(treeService.serviceDescription)
            tietPrice.setText(treeService.price)
            tietAvailability.setText(treeService.availabilty)
        }

        dialog = if(newTreeService)
            buildDialog(getString(R.string.save), getString(R.string.cancel),{
                // Acción de guardar
                // Aquí obetenemos los textos ingresados y se los asignamos a nuestro objeto treeService

                binding.apply {
                    treeService.apply {
                        serviceTitle = tietServiceTitle.text.toString()
                        serviceDescription = tietServiceDescription.text.toString()
                        price = tietPrice.text.toString().toIntOrNull() ?: 0
                        availabilty = tietAvailability.text.toString()
                    }
                }

                try {
                    lifecycleScope.launch(Dispatchers.IO){
                        val result = async{
                            repository.insertTreeService(treeService)
                        }

                        // Con esto nos esperamos a que se termine esta acción antes de ejecutar lo siguiente
                        result.await()

                        // Con esto mandamos la ejecución de message y updateUI al hilo principal
                        withContext(Dispatchers.Main){
                            message(getString(R.string.save_message))

                            updateUI()
                        }
                    }
                }catch (e: IOException){
                    message(getString(R.string.save_error_message))
                }
            }, {
                // Acción de cancelar
            })
        else
            buildDialog("Actualizar", "Borrar", {
                // Acción de actualizar
                // Aquí obetenemos los textos ingresados y se los asignamos a nuestro objeto treeService
                binding.apply {
                    treeService.apply {
                        serviceTitle = tietServiceTitle.text.toString()
                        serviceDescription = tietServiceDescription.text.toString()
                        price = tietPrice.text.toString().toIntOrNull() ?: 0
                        availabilty = tietAvailability.text.toString()
                    }
                }

                try {
                    lifecycleScope.launch(Dispatchers.IO){
                        val result = async {
                            repository.updateTreeService(treeService)
                        }
                        result.await()

                        withContext(Dispatchers.Main){
                            message(getString(R.string.service_updated))

                            updateUI()
                        }
                    }
                }catch (e: IOException){
                    message(getString(R.string.service_update_error_message))
                }


            },{
                // Acción de borrar
                // Almacenamos el contexto en una variable antes de mandar llamar el diálogo nuevo

                val context = requireContext()

                AlertDialog.Builder(requireContext())
                    .setTitle((getString(R.string.confirm)))
                    .setMessage(getString(R.string.confirm_message, treeService.serviceTitle))
                    .setPositiveButton(getString(R.string.ok)){ _, _ ->
                        try {
                            lifecycleScope.launch(Dispatchers.IO){
                                val result = async {
                                    repository.deleteTreeService(treeService)
                                }
                                result.await()

                                withContext(Dispatchers.Main){
                                    message(context.getString(R.string.service_deleted))

                                    updateUI()

                                }
                            }

                        }catch (e: IOException){
                            message(getString(R.string.delete_service_error))

                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)){ dialog, _->
                        dialog.dismiss()
                    }
                    .create().show()
            })


        return dialog
    }

    // Aquí se destruye
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Se llama después de que se muestra el dialog en pantalla
    override fun onStart() {
        super.onStart()

        // Debido a que la clase dialog no me permite referenciarme a sus botones
        val alertDialog = dialog as AlertDialog

        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        saveButton?.isEnabled = false

        binding.apply {
            setupTextWatcher(
                tietServiceTitle,
                tietServiceDescription,
                tietPrice,
                tietAvailability
            )
        }

    }

    private fun validateFields(): Boolean
        = binding.tietServiceTitle.text.toString().isNotEmpty() &&
            binding.tietServiceDescription.text.toString().isNotEmpty() &&
            binding.tietPrice.text.toString().isNotEmpty() &&
            binding.tietAvailability.text.toString().isNotEmpty()

    private fun setupTextWatcher(vararg textFields: TextInputEditText){
        val textWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        }

        textFields.forEach { textField ->
            textField.addTextChangedListener(textWatcher)
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
            .setPositiveButton(btn1Text){_, _ ->
                positiveButton()
            } .setNegativeButton(btn2Text){_, _ ->
                negativeButton()
            }
            .create()

}