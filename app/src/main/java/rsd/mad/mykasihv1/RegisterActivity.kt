package rsd.mad.mykasihv1

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.databinding.ActivityRegisterBinding
import rsd.mad.mykasihv1.models.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.registration)

        auth = Firebase.auth
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegister.setOnClickListener { register() }
        binding.hplLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private var name = ""
    private var email = ""
    private var mobile = ""
    private var password = ""
    private var role = ""
    private var house = 0
    private var street = ""
    private var city = ""

    private fun register() {
        // Validate Data
        var isValid = true

        with(binding) {
            name = edtNameRegister.text.toString().trim()
            email = edtEmailRegister.text.toString().trim()
            mobile = edtMobileRegister.text.toString().trim()
            password = edtPasswordRegister.text.toString()
            role = if (rbDonor.isChecked) "Donor" else "Donee"
            house = edtHouseRegister.text.toString().toIntOrNull() ?: 0
            street = edtStreetRegister.text.toString().trim()
            city = edtCityRegister.text.toString().trim()

            if (name.isEmpty()) {
                edtNameRegister.error = getString(R.string.err_empty)
                isValid = false
            }

            if (email.isEmpty()) {
                edtEmailRegister.error = getString(R.string.err_empty)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmailRegister.error = getString(R.string.err_email)
                isValid = false
            }

            val phoneRegex = Regex("^01\\d-\\d{7,8}$")
            if (mobile.isEmpty()) {
                edtMobileRegister.error = getString(R.string.err_empty)
                isValid = false
            } else if (!phoneRegex.matches(mobile)) {
                edtMobileRegister.error = getString(R.string.err_mobile)
                isValid = false
            }

            val passwordRegex = Regex(".{6,}")
            if (password.isEmpty()) {
                edtPasswordRegister.error = getString(R.string.err_empty)
                isValid = false
            } else if (!passwordRegex.matches(password)) {
                edtPasswordRegister.error = getString(R.string.err_password)
                isValid = false
            }

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
                                    var i = Intent(this@RegisterActivity, MainActivity::class.java
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
                            is FirebaseAuthWeakPasswordException -> {
                                toast("Password should be at least of 6 characters")
                                edtPasswordRegister.error = "Password should be at least of 6 characters"
                            }
                            is FirebaseAuthUserCollisionException -> toast("This email is already in use")
                            is FirebaseAuthInvalidCredentialsException -> toast("Please enter a valid email")
                            else -> toast("Error")
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            val database = Firebase.database
            var ref = database.getReference("Users").child("Donor")
            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (value in snapshot.children) {
                        if (value.key == auth.currentUser?.uid) {
                            startActivity(Intent(applicationContext, DonorDashboardActivity::class.java))
                            finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            ref = database.getReference("Users").child("Donee")
            ref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (value in snapshot.children) {
                        if (value.key == auth.currentUser?.uid) {
                            startActivity(Intent(applicationContext, DoneeDashboardActivity::class.java))
                            finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}