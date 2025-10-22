package com.foodapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.foodapp.databinding.FragmentLoginBinding
import com.foodapp.ui.auth.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

import com.foodapp.R



class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Login button
        binding.btnLogin.setOnClickListener { login() }

        // 🔹 Register navigation button
        binding.tvGoRegister.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.authContainer, RegisterFragment())
                .addToBackStack(null)     // optional so user can go back
                .commit()
        }

        return binding.root
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Enter email & password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}