package com.example.uasmobileprogramming

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Adjust padding to accommodate system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("users")

        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Set custom text with clickable part
        val text = "Don't have an account? Register"
        val spannableString = SpannableString(text)

        // Define the clickable span
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@Login, Register::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false 
            }
        }



        // Set the clickable span to the "Register" part of the text
        spannableString.setSpan(clickableSpan, text.indexOf("Register"), text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Make the "Register" part bold
        spannableString.setSpan(StyleSpan(Typeface.BOLD), text.indexOf("Register"), text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the text and enable link movement
        tvRegister.text = spannableString
        tvRegister.movementMethod = LinkMovementMethod.getInstance()

        // Set onClickListener for Login Button
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
            } else {
                // Check Firebase for user login credentials
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child(username).exists()) {
                            val storedPassword = snapshot.child(username).child("password").getValue(String::class.java)
                            if (storedPassword == password) {
                                Toast.makeText(applicationContext, "Login Berhasil", Toast.LENGTH_SHORT).show()
                                val berhasil = Intent(applicationContext, MainActivity::class.java)
                                startActivity(berhasil)
                            } else {
                                Toast.makeText(applicationContext, "Password Salah", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Data Belum Terdaftar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle jika ada error saat mengambil data
                        Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
