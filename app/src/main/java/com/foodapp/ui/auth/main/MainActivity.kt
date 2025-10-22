package com.foodapp.ui.auth.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodapp.R
import com.foodapp.ui.auth.AuthActivity
import com.foodapp.ui.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)   // uses res/layout/activity_main.xml

        // If not logged in, go back to Auth
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        // Show ProfileFragment in the container
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ProfileFragment())
                .commit()
        }
    }
}
