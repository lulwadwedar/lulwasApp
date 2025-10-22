package com.foodapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.foodapp.databinding.FragmentRegisterBinding
import com.foodapp.model.UserProfile
import com.foodapp.ui.auth.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.foodapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference
        binding.btnRegister.setOnClickListener { register() }
        binding.tvGoLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authContainer, LoginFragment())
                .commit()
        }
        return binding.root
    }

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (email.isEmpty() || password.length < 6) {
            Toast.makeText(requireContext(), "Enter a valid email & 6+ char password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val profile = UserProfile(uid = uid, name = name, phone = phone)
                db.child("users").child(uid).setValue(profile)
                    .addOnSuccessListener {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "DB error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
