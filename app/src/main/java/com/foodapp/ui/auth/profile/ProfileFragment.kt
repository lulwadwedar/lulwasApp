package com.foodapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.foodapp.databinding.FragmentProfileBinding
import com.foodapp.model.UserProfile
import com.foodapp.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {   // ✅ Must extend Fragment

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference   // uses your default DB

        // --- listeners ---
        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnSignOut.setOnClickListener { signOut() }

        loadProfile()
        return binding.root
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    val p = s.getValue(UserProfile::class.java)
                    binding.etName.setText(p?.name ?: "")
                    binding.etPhone.setText(p?.phone ?: "")
                }
                override fun onCancelled(e: DatabaseError) {
                    Toast.makeText(requireContext(), "Load error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "Not signed in", Toast.LENGTH_SHORT).show()
            return
        }

        val updated = UserProfile(
            uid = uid,
            name = binding.etName.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim()
        )

        binding.btnSave.isEnabled = false
        db.child("users").child(uid).setValue(updated)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Saved ✅", Toast.LENGTH_SHORT).show()
                binding.btnSave.isEnabled = true
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnSave.isEnabled = true
            }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        requireActivity().finish() // close MainActivity so back doesn’t return here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}