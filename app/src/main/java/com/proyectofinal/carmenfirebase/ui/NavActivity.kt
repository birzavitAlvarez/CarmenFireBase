package com.proyectofinal.carmenfirebase.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.proyectofinal.carmenfirebase.R
import com.proyectofinal.carmenfirebase.databinding.ActivityNavBinding
import com.proyectofinal.carmenfirebase.ui.fragmets.BorrarFragment
import com.proyectofinal.carmenfirebase.ui.fragmets.LoginFragment
import com.proyectofinal.carmenfirebase.ui.fragmets.RecuperarFragment
import com.proyectofinal.carmenfirebase.ui.fragmets.GaleryFragment

class NavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavBinding
    //
    private lateinit var email: String
    private lateinit var provider: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle = intent.extras
        email = bundle?.getString("email").toString()
        provider = bundle?.getString("provider").toString()

//        replaceFragment(LoginFragment())
        replaceFragment(LoginFragment.newInstance(email, provider))
        binding.bottomNavigationView.setOnItemReselectedListener {
            when(it.itemId){
                R.id.navHome -> replaceFragment(LoginFragment.newInstance(email, provider))
                R.id.navBorrar -> replaceFragment(BorrarFragment.newInstance(email, provider))
                R.id.navGalery -> replaceFragment(GaleryFragment.newInstance(email, provider))
                R.id.navRecuperar -> replaceFragment(RecuperarFragment.newInstance(email, provider))
            }
        }
        true
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayouts,fragment)
        fragmentTransaction.commit()
    }
}