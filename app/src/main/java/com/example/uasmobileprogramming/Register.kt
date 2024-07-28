package com.example.uasmobileprogramming

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Adjust padding to accommodate system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("users")

        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        // Set custom text with clickable part
        val text = "Already have an account? Login"
        val spannableString = SpannableString(text)

        // Define the clickable span
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@Register, Login::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        // Set the clickable span to the "Login" part of the text
        spannableString.setSpan(clickableSpan, text.indexOf("Login"), text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Make the "Login" part bold
        spannableString.setSpan(StyleSpan(Typeface.BOLD), text.indexOf("Login"), text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the text and enable link movement
        tvLogin.text = spannableString
        tvLogin.movementMethod = LinkMovementMethod.getInstance()

        // Handle register button click
        btnRegister.setOnClickListener {
            val username = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Isi data yang kosong", Toast.LENGTH_SHORT).show()
            } else {
                // Register user in Firebase
                database.child(username).child("username").setValue(username)
                database.child(username).child("email").setValue(email)
                database.child(username).child("password").setValue(password)

                Toast.makeText(applicationContext, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()

                // Navigate to login screen
                val register = Intent(applicationContext, Login::class.java)
                startActivity(register)
            }
        }
    }
}
