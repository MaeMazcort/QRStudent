
package com.example.qrstudent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrstudent.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth before checking for current user
        auth = FirebaseAuth.getInstance()

        // Check if the intent has the LOGOUT flag
        if (intent.getBooleanExtra("LOGOUT", false)) {
            auth.signOut()
        } else {
            // Check if user is already signed in
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is already authenticated, redirect to appropriate dashboard
                redirectBasedOnUserRole(currentUser.uid)
                return  // Exit onCreate to prevent loading the login UI
            }
        }

        // If no active session or user logged out, continue with normal login UI setup
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid
                        uid?.let {
                            redirectBasedOnUserRole(uid)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun redirectBasedOnUserRole(uid: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.child("role").getValue(String::class.java)
                val name = snapshot.child("name").getValue(String::class.java)

                Toast.makeText(this@LoginActivity, "Welcome $name ($role)", Toast.LENGTH_SHORT).show()

                val intent = when (role) {
                    "Admin" -> Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                    "Profesor" -> Intent(this@LoginActivity, ProfesorDashboardActivity::class.java)
                    "Alumno" -> Intent(this@LoginActivity, AlumnoDashboardActivity::class.java)
                    else -> Intent(this@LoginActivity, LoginActivity::class.java)
                }

                startActivity(intent)
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Error retrieving user data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
