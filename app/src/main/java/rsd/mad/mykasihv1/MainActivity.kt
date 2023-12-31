package rsd.mad.mykasihv1

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import rsd.mad.mykasihv1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var database = Firebase.database
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth
        sharedPref =
            this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        if (auth.currentUser != null) {
            val role = sharedPref.getString(getString(R.string.role), null)
            if (role == getString(R.string.donor)) {
                startActivity(Intent(this, DonorDashboardActivity::class.java))
                this.finish()
            } else if (role == getString(R.string.donee)){
                startActivity(Intent(this, DoneeDashboardActivity::class.java))
                this.finish()
            }

//            if (sharedPref.getString(
//                    getString(R.string.role),
//                    getString(R.string.donor)
//                ) == getString(R.string.donor)
//            ) {
//                startActivity(Intent(this, DonorDashboardActivity::class.java))
//                this.finish()
//            } else {
//                startActivity(Intent(this, DoneeDashboardActivity::class.java))
//                this.finish()
//            }
//            auth.signOut()
        }

        binding.btnLogin.setOnClickListener { login() }
        binding.btnForgotPass.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
        }
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
        progressDialog.setMessage("Logging in...")
        progressDialog.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    var ref = database.getReference("Users").child("Donor")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (value in snapshot.children) {
                                if (value.key == auth.currentUser?.uid) {
                                    progressDialog.dismiss()

                                    generateDeviceToken(value.key!!, "Donor")

                                    val name = value.child("name").value.toString()
                                    val mobile = value.child("mobile").value.toString()
                                    val address = value.child("address").value.toString()
                                    val city = value.child("city").value.toString()
                                    val profileImage = value.child("profileImage").value.toString()
                                    var point = value.child("point").value.toString().toInt()
                                    var device = value.child("token").value.toString()

                                    with(sharedPref.edit()) {
                                        putString(getString(R.string.name), name)
                                        putString(getString(R.string.email), email)
                                        putString(getString(R.string.mobile), mobile)
                                        putString(getString(R.string.address), address)
                                        putString(getString(R.string.city), city)
                                        putString(
                                            getString(R.string.role),
                                            getString(R.string.donor)
                                        )
                                        putString(getString(R.string.password), password)
                                        putString(getString(R.string.profileImage), profileImage)
                                        putInt(getString(R.string.point), point)
                                        putString(getString(R.string.device_token), device)
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
                                    progressDialog.dismiss()

                                    generateDeviceToken(value.key!!, "Donee")

                                    val name = value.child("name").value.toString()
                                    val mobile = value.child("mobile").value.toString()
                                    val address = value.child("address").value.toString()
                                    val city = value.child("city").value.toString()
                                    val profileImage = value.child("profileImage").value.toString()
                                    var device = value.child("token").value.toString()

                                    with(sharedPref.edit()) {
                                        putString(getString(R.string.name), name)
                                        putString(getString(R.string.email), email)
                                        putString(getString(R.string.mobile), mobile)
                                        putString(getString(R.string.address), address)
                                        putString(getString(R.string.city), city)
                                        putString(
                                            getString(R.string.role),
                                            getString(R.string.donee)
                                        )
                                        putString(getString(R.string.password), password)
                                        putString(getString(R.string.profileImage), profileImage)
                                        putString(getString(R.string.device_token), device)
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
                    progressDialog.dismiss()
                    if (task.exception is FirebaseAuthInvalidUserException)
                        toast("User does not exist")
                    else if (task.exception is FirebaseAuthInvalidCredentialsException)
                        toast("Invalid email/password")
//                    else
//                        toast("Error")
                }
            }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun generateDeviceToken(uid: String, role: String) {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                toast("Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val token = task.result
            val tokenRef = database.getReference("Users").child(role).child(uid)
            tokenRef.child("device").setValue(token.toString())
        }
    }
}