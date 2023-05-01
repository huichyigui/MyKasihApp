package rsd.mad.mykasihv1

import android.app.Application
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Helper:Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
            return format.format(date)
        }

        fun loadDoneeCity(doneeId : String, tvCity : TextView) {
            val ref = Firebase.database.getReference("Users").child("Donee")
            ref.child(doneeId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val city = snapshot.child("city").value.toString()
                        tvCity.text = city
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }
}