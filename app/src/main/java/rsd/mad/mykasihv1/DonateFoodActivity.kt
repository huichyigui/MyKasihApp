package rsd.mad.mykasihv1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.databinding.ActivityDonateFoodBinding
import rsd.mad.mykasihv1.models.Donation
import java.util.*
import java.util.Calendar.*

class DonateFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonateFoodBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var doneeId = ""
    private var requestId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonateFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        doneeId = intent.getStringExtra(getString(R.string.donee_id)) ?: ""
        requestId = intent.getLongExtra(getString(R.string.timestamp), 0).toString()

        with(binding) {
            edtLocation.setText("${sharedPref.getString(getString(R.string.address), "")} ${sharedPref.getString(getString(R.string.city), "")}")
            val calendar = Calendar.getInstance()
            btnDate.setOnClickListener {
                val year = calendar.get(YEAR)
                val month = calendar.get(MONTH)
                val day = calendar.get(DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@DonateFoodActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        btnDate.text = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    },
                    year,
                    month,
                    day
                )

                val now = System.currentTimeMillis()
                datePickerDialog.datePicker.minDate = now
                datePickerDialog.datePicker.maxDate =
                    now + (1000 * 60 * 60 * 24 * 7) // After 7 Days from Now
                datePickerDialog.show()
            }
            btnTime.setOnClickListener {
                val hour = calendar.get(HOUR_OF_DAY)
                val minute = calendar.get(MINUTE)
                val timePickerDialog = TimePickerDialog(
                    this@DonateFoodActivity,
                    { view, hour, minute ->
                        var am_pm = if (hour < 12)
                            "AM"
                        else
                            "PM"
                        btnTime.text = String.format("%2d:%2d %s", hour, minute, am_pm)
                    }, hour, minute, false
                )
                timePickerDialog.show()
            }
            btnSubmit.setOnClickListener { validateData() }
        }
    }

    private var category = ""
    private var packaging = ""
    private var amountUnit = 0
    private var amountKg = 0.0
    private var amountRm = 0.0
    private var amount = ""
    private var date = ""
    private var time = ""
    private var location = ""
    private var status = "Pending"
    private fun validateData() {
        var isValid = true

        with(binding) {
            category = spCategory.selectedItem as String
            packaging = spPackaging.selectedItem as String
            date = btnDate.text.toString()
            time = btnTime.text.toString()
            location = edtLocation.text.toString().trim()

            if (edtUnit.text.isNotBlank() && edtKg.text.isBlank() && edtRm.text.isBlank()) {
                amountUnit = edtUnit.text.toString().toIntOrNull() ?: -1
                if (amountUnit < 0) {
                    edtUnit.error = getString(R.string.err_unit)
                    isValid = false
                } else {
                    amount = "$amountUnit unit"
                }
            } else if (edtUnit.text.isBlank() && edtKg.text.isNotBlank() && edtRm.text.isBlank()) {
                amountKg = edtKg.text.toString().toDoubleOrNull() ?: -1.0
                if (amountKg < 0.0) {
                    edtKg.error = getString(R.string.err_kg)
                    isValid = false
                } else {
                    amount = "$amountKg kg"
                }
            } else if (edtUnit.text.isBlank() && edtKg.text.isBlank() && edtRm.text.isNotBlank()) {
                amountRm = edtRm.text.toString().toDoubleOrNull() ?: -1.0
                if (amountRm < 0.0) {
                    edtRm.error = getString(R.string.err_rm)
                    isValid = false
                } else {
                    amount = "RM ${"%.2f".format(amountRm)}"
                }
            } else {
                toast(getString(R.string.err_contribution))
                isValid = false
            }

            if (date == "dd/MM/yyyy") {
                toast("Missing Preferred Date")
                isValid = false
            }
            if (time == "hh:mm") {
                toast("Missing Preferred Time")
                isValid = false
            }
            if (location.isEmpty()) {
                edtLocation.error = getString(R.string.err_empty)
                isValid = false
            }
            if (isValid) {
                donateFood()
            }
        }
    }

    private fun donateFood() {
        val token = Helper.generateRandomString()
        val timestamp = System.currentTimeMillis()

        val database = Firebase.database
        val donation = Donation(auth.uid!!, doneeId, requestId, category, packaging, amount, date, time, location, status, token, timestamp)

        var ref = database.getReference("Donation").push()
        ref.setValue(donation)
        toast("Your Donation Waiting For Its Donee")
        startActivity(Intent(this, DonorDashboardActivity::class.java))
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}