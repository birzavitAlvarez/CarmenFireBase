package com.proyectofinal.carmenfirebase.ui.fragmets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.proyectofinal.carmenfirebase.databinding.FragmentBorrarBinding


class BorrarFragment : Fragment() {

    companion object {
        private const val ARG_EMAIL = "email"
        private const val ARG_PROVIDER = "provider"

        fun newInstance(email: String, provider: String): BorrarFragment {
            val fragment = BorrarFragment()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            args.putString(ARG_PROVIDER, provider)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentBorrarBinding? = null
    private val binding get() = _binding!!

    //abrir la conexion para la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBorrarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString(BorrarFragment.ARG_EMAIL, "")
        val provider = arguments?.getString(BorrarFragment.ARG_PROVIDER, "")

        binding.btnEliminar.setOnClickListener {
            eliminar(email ?: "")
        }


    }

    private fun eliminar(email:String) {
        db.collection("users").document(email).delete()
        var mensajeborrar = Toast.makeText(
            requireContext(),
            "Los datos han sido eliminados correctamente",
            Toast.LENGTH_LONG
        )
        mensajeborrar.show()
    }


}