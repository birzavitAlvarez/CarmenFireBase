package com.proyectofinal.carmenfirebase.ui.fragmets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.proyectofinal.carmenfirebase.R
import com.proyectofinal.carmenfirebase.databinding.FragmentRecuperarBinding


class RecuperarFragment : Fragment() {

    companion object {
        private const val ARG_EMAIL = "email"
        private const val ARG_PROVIDER = "provider"

        fun newInstance(email: String, provider: String): RecuperarFragment {
            val fragment = RecuperarFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            args.putString(ARG_PROVIDER, provider)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentRecuperarBinding? = null
    private val binding get() = _binding!!
    //abrir la conexion para la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecuperarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString(RecuperarFragment.ARG_EMAIL, "")
        val provider = arguments?.getString(RecuperarFragment.ARG_PROVIDER, "")

        binding.btnRecuperarDatos.setOnClickListener {
            recuperarData(email ?: "", provider ?: "")
        }
    }

    private fun recuperarData(email: String, provider: String) {
        db.collection("users").document(email).get().addOnSuccessListener { documentSnapshot ->
            val address = documentSnapshot.getString("address") ?: ""
            val phone = documentSnapshot.getString("phone") ?: ""

            binding.tietEmailRecuperar.setText(email)
            binding.tietProviderRecuperar.setText(provider)
            binding.tietAddressRecuperar.setText(address)
            binding.tietPhoneRecuperar.setText(phone)
        }
    }

}