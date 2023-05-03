package rsd.mad.mykasihv1

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.databinding.ActivityRegister2Binding
import rsd.mad.mykasihv1.models.User

class Register2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegister2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.address_info)

        auth = Firebase.auth
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegister.setOnClickListener { register() }
        binding.hplLogin2.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private var house = 0
    private var street = ""
    private var city = ""

    private fun register() {
        // Validate Data
        var isValid = true

        with(binding) {
            house = edtHouseRegister.text.toString().toIntOrNull() ?: 0
            street = edtStreetRegister.text.toString().trim()
            city = edtCityRegister.text.toString().trim()

            if (house <= 0) {
                edtHouseRegister.error = getString(R.string.err_house)
                isValid = false
            }

            if (street.isEmpty()) {
                edtStreetRegister.error = getString(R.string.err_empty)
                isValid = false
            }

            if (city.isEmpty()) {
                edtCityRegister.error = getString(R.string.err_empty)
                isValid = false
            }

            if (isValid) {
                progressDialog.setMessage("Creating account...")
                progressDialog.show()

                val name = intent.getStringExtra(getString(R.string.name)) ?: ""
                val mobile = intent.getStringExtra(getString(R.string.mobile)) ?: ""
                val email = intent.getStringExtra(getString(R.string.email)) ?: ""
                val password = intent.getStringExtra(getString(R.string.password)) ?: ""
                val role = intent.getStringExtra(getString(R.string.role)) ?: ""
                val address = "$house $street"


                progressDialog.setMessage("Saving user info")

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user: User = User(name, email, mobile, city, address)
                        val database = Firebase.database

                        auth.currentUser?.let { it1 ->
                            database.getReference("Users").child(role).child(it1.uid)
                                .setValue(user)
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    toast("User registered")
                                    var i = Intent(this@Register2Activity, MainActivity::class.java
                                    )
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(i)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    progressDialog.dismiss()
                                    toast("Database error")
                                }
                        }
                    } else {
                        progressDialog.dismiss()
                        when (it.exception) {
                            is FirebaseAuthWeakPasswordException -> toast("Password should be at least of 6 characters")
                            is FirebaseAuthUserCollisionException -> toast("This email is already in use")
                            is FirebaseAuthInvalidCredentialsException -> toast("Please enter a valid email")
                            else -> toast("Error")
                        }
                    }
                }
            }
        }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }


}
