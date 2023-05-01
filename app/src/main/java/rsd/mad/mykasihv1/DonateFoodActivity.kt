package rsd.mad.mykasihv1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import rsd.mad.mykasihv1.databinding.ActivityDonateFoodBinding
import java.util.*
import java.util.Calendar.*

class DonateFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonateFoodBinding
    private var doneeId = ""
    private var timestamp = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonateFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doneeId = intent.getStringExtra(getString(R.string.donee_id)) ?: ""
        timestamp = intent.getLongExtra(getString(R.string.timestamp), 0).toInt()

        with(binding) {
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

    private fun validateData() {
        TODO("Not yet implemented")
    }
}