package com.proyectofinal.carmenfirebase

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.proyectofinal.carmenfirebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //cargar imagen
    private var currentFile: Uri? = null
    private var imageReference = Firebase.storage.reference
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.data?.data?.let {
                    currentFile = it
                    binding.ivStorage.setImageURI(it)
                }
            } else {
                Toast.makeText(this, "Operacion Cancelada", Toast.LENGTH_SHORT).show()
            }
        }

    //abrir la conexion para la base de datos
    private val db = FirebaseFirestore.getInstance()

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        var bundle = intent.extras
        email = bundle?.getString("email").toString()
        var provider = bundle?.getString("provider")

        //en el caso de que no existan valores se enviaran vacios
        setup(email ?: "", provider ?: "")
        jalarDatos()
        //

        // aqui abre la galeria del dispositivo
        binding.ivStorage.setOnClickListener {
            //
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "images/*"
                imageLauncher.launch(it)
            }
        }

        binding.btnUpload.setOnClickListener {
            // 1 al nombre del archivo de momento.
            uploadImagetoStorage("11")
        }
    }

    // cargar imagen
    private fun uploadImagetoStorage(filename: String) {
        try {
            currentFile?.let {
                imageReference.child("images/$filename").putFile(it).addOnCompleteListener{
                    Toast.makeText(this, "Carga Exitosa", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "Error al cargar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    //
    //

    //
    private fun setup(email: String, provider: String) {
        binding.emailTextView.text = email
        binding.providerTextView.text = provider

        binding.LogOutButton.setOnClickListener() {
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        //contianuamos con la programación del boton Save
        binding.saveButton.setOnClickListener() {
            if (binding.addressTextView.text.isNotEmpty() && binding.phoneTextView.text.isNotEmpty()) {
                db.collection("users").document(email).set(
                    hashMapOf(
                        "provider" to provider,
                        "address" to binding.addressTextView.text.toString(),
                        "phone" to binding.phoneTextView.text.toString()
                    )
                )
                var toast = Toast.makeText(this, "Los datos han sido guardados", Toast.LENGTH_LONG)
                toast.show()
            } else {
                var mensaje = Toast.makeText(this, "Faltan datos", Toast.LENGTH_LONG)
                mensaje.show()
            }
        }

        binding.editButton.setOnClickListener() {
            jalarDatos()
        }

        binding.deleteButton.setOnClickListener() {
            db.collection("users").document(email).delete()
            var mensajeborrar = Toast.makeText(
                this,
                "Los datos han sido eliminados correctamente",
                Toast.LENGTH_LONG
            )
            limpiarData()
            mensajeborrar.show()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun jalarDatos() {
        db.collection("users").document(email).get().addOnSuccessListener { documentSnapshot ->
            val address = documentSnapshot.getString("address") ?: ""
            val phone = documentSnapshot.getString("phone") ?: ""
            val imagen = documentSnapshot.getString("imagen") ?: ""

            binding.addressTextView.setText(address)
            binding.phoneTextView.setText(phone)
        }
    }
    //

    private fun limpiarData() {
        binding.addressTextView.setText("")
        binding.phoneTextView.setText("")
    }


}