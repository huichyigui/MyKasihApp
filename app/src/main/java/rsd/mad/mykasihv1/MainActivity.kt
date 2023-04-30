package rsd.mad.mykasihv1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (auth.currentUser != null)
            auth.signOut()

        binding.btnLogin.setOnClickListener { login() }
        binding.hplRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private var email = ""
    private var password = ""

    private fun login() {
        var isValid = true

        with(binding) {
            email = edtEmailLogin.text.toString().trim()
            password = edtPasswordLogin.text.toString()

            if (email.isEmpty()) {
                edtEmailLogin.error = getString(R.string.err_empty)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtPasswordLogin.error = getString(R.string.err_email)
                isValid = false
            }

            if (password.isEmpty()) {
                edtPasswordLogin.error = getString(R.string.err_empty)
                isValid = false
            }

            if (isValid) {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val database = Firebase.database

                    var ref = database.getReference("Users").child("Donor")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (value in snapshot.children) {
                                if (value.key == auth.currentUser?.uid) {
                                    val name = value.child("name").value.toString()
                                    val mobile = value.child("mobile").value.toString()
                                    val address = value.child("address").value.toString()

                                    with(sharedPref.edit()) {
                                        putString(getString(R.string.name), name)
                                        putString(getString(R.string.mobile), mobile)
                                        putString(getString(R.string.address), address)
                                        putString(getString(R.string.role), getString(R.string.donor))
                                        apply()
                                    }

                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            DonorDashboardActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

                    ref = database.getReference("Users").child("Donee")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (value in snapshot.children) {
                                if (value.key == auth.currentUser?.uid) {
                                    val name = value.child("name").value.toString()
                                    val mobile = value.child("mobile").value.toString()
                                    val address = value.child("address").value.toString()

                                    with(sharedPref.edit()) {
                                        putString(getString(R.string.name), name)
                                        putString(getString(R.string.mobile), mobile)
                                        putString(getString(R.string.address), address)
                                        putString(getString(R.string.role), getString(R.string.donee))
                                        putString(getString(R.string.password), password)
                                        apply()
                                    }

                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            DoneeDashboardActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException)
                        toast("User does not exist")
                    else
                        toast("Error")
                }
            }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}