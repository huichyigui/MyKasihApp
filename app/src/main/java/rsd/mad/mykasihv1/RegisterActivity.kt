package rsd.mad.mykasihv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.registration)

        auth = Firebase.auth

        binding.btnNext.setOnClickListener { register2() }
        binding.hplLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private var name = ""
    private var email = ""
    private var mobile = ""
    private var password = ""
    private var role = ""

    private fun register2() {
        // Validate Data
        var isValid = true
        val intent = Intent(this, Register2Activity::class.java)

        with(binding) {
            name = edtNameRegister.text.toString().trim()
            email = edtEmailRegister.text.toString().trim()
            mobile = edtMobileRegister.text.toString().trim()
            password = edtPasswordRegister.text.toString()
            role = if (rbDonor.isChecked) "Donor" else "Donee"

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

            if (mobile.isEmpty()) {
                edtMobileRegister.error = getString(R.string.err_empty)
                isValid = false
            } else if (!Patterns.PHONE.matcher(mobile).matches()) {
                edtMobileRegister.error = getString(R.string.err_mobile)
                isValid = false
            }

            if (password.isEmpty()) {
                edtPasswordRegister.error = getString(R.string.err_empty)
                isValid = false
            }

            if (isValid) {
                intent.putExtra(getString(R.string.name), name)
                intent.putExtra(getString(R.string.email), email)
                intent.putExtra(getString(R.string.mobile), mobile)
                intent.putExtra(getString(R.string.password), password)
                intent.putExtra(getString(R.string.role), role)

                startActivity(intent)
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
                    TODO("Not yet implemented")
                }

            })
        }
    }
}