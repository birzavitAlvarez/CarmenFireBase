package com.proyectofinal.carmenfirebase.ui.fragmets

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.carmenfirebase.ImagesActivity
import com.proyectofinal.carmenfirebase.R
import com.proyectofinal.carmenfirebase.databinding.FragmentGaleryBinding


class GaleryFragment : Fragment() {

    companion object {
        private const val ARG_EMAIL = "email"
        private const val ARG_PROVIDER = "provider"

        fun newInstance(email: String, provider: String): GaleryFragment {
            val fragment = GaleryFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            args.putString(ARG_PROVIDER, provider)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentGaleryBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGaleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString(GaleryFragment.ARG_EMAIL, "")
        val provider = arguments?.getString(GaleryFragment.ARG_PROVIDER, "")
        initVars()
        registerClickEvents()

    }

    private fun registerClickEvents() {
        binding.uploadBtn.setOnClickListener {
            uploadImage()
        }

        binding.showAllBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ImagesActivity::class.java))
        }

        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        imageUri = it
        binding.imageView.setImageURI(it)
    }


    private fun initVars() {

        storageRef = FirebaseStorage.getInstance().reference.child("Images2")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()

                        firebaseFirestore.collection("images2").add(map).addOnCompleteListener { firestoreTask ->

                            if (firestoreTask.isSuccessful){
                                Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(requireContext(), firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()

                            }
                            binding.progressBar.visibility = View.GONE
                            binding.imageView.setImageResource(R.drawable.vector)

                        }
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.imageView.setImageResource(R.drawable.vector)
                }
            }
        }
    }






}