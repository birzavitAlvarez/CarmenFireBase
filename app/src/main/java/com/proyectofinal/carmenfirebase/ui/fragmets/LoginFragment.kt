package com.proyectofinal.carmenfirebase.ui.fragmets

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.proyectofinal.carmenfirebase.LoginActivity
import com.proyectofinal.carmenfirebase.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    companion object {
        private const val ARG_EMAIL = "email"
        private const val ARG_PROVIDER = "provider"

        fun newInstance(email: String, provider: String): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            args.putString(ARG_PROVIDER, provider)
            fragment.arguments = args
            return fragment
        }
    }


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    //cargar imagen
    private var currentFile: Uri? = null
    private var imageReference = Firebase.storage.reference
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result?.data?.data?.let {
                    currentFile = it
                    binding.ivFoto.setImageURI(it)
                }
            } else {
                Toast.makeText(requireContext(), "Operacion Cancelada", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString(ARG_EMAIL, "")
        val provider = arguments?.getString(ARG_PROVIDER, "")
        binding.tvUser.text = email ?: ""

        binding.btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }
        //
        cargarFotoExistente(email ?: "")
        //
        binding.ivFoto.setOnClickListener {
            openGallery()
        }

        binding.btnUpdatePhoto.setOnClickListener {
            uploadImagetoStorage(email ?: "")
        }

    }

    private fun cargarFotoExistente(email:String) {
        val filename = "images/$email"
        val storageReference = Firebase.storage.getReference(filename)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.ivFoto)
        }.addOnFailureListener { exception ->
            // Manejar el error al obtener la URL de la imagen
            Toast.makeText(requireContext(), "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // cargar imagen
//    private fun uploadImagetoStorage(filename: String) {
//        try {
//            currentFile?.let {
//                imageReference.child("images/$filename").putFile(it)
//                    .addOnSuccessListener {
//                        requireActivity().runOnUiThread {
//                            Log.d("Upload", "Carga Exitosa")
//                            Toast.makeText(
//                                requireContext(),
//                                "Carga Exitosa",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                    .addOnFailureListener {
//                        requireActivity().runOnUiThread {
//                            Log.e("Upload", "Error al cargar el archivo", it)
//                            Toast.makeText(
//                                requireContext(),
//                                "Error al cargar el archivo",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//            }
//        } catch (e: Exception) {
//            Log.e("Upload", "Error: $e")
//            Toast.makeText(requireContext(), "Error: $e", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun uploadImagetoStorage(filename: String) {
        try {
            currentFile?.let {
                imageReference.child("images/$filename").putFile(it)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Carga Exitosa", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al cargar el archivo", Toast.LENGTH_SHORT).show()
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }





    // TODO PERMISOS Y VERIFICO
    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionExplanationDialog()
            }

            else -> {
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso concedido
                openGallery()
            } else {
                // Permiso denegado
                Toast.makeText(
                    requireContext(),
                    "Permiso denegado. No se puede acceder a la galería.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            imageLauncher.launch(it)
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permiso necesario")
            .setMessage("Se requiere permiso para acceder a la galería y seleccionar una imagen.")
            .setPositiveButton("Aceptar") { _, _ ->
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Permiso denegado. No se puede acceder a la galería.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
    // TODO FIN


}