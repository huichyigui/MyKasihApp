package rsd.mad.mykasihv1

import android.app.Application
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.models.Donation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

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
                    }
                })
        }

        fun loadDoneeName(doneeId: String, tvDoneeNameDonation: TextView) {
            val ref = Firebase.database.getReference("Users").child("Donee")
            ref.child(doneeId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val doneeName = snapshot.child("name").value.toString()
                        tvDoneeNameDonation.text = doneeName
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun loadDonorName(donorId: String, tvDonorNameDonation: TextView) {
            val ref = Firebase.database.getReference("Users").child("Donor")
            ref.child(donorId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val doneeName = snapshot.child("name").value.toString()
                        tvDonorNameDonation.text = doneeName
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun loadPax(doneeId: String, requestId: String, tvPax : TextView) {
            val ref = Firebase.database.getReference("RequestDonation").child(doneeId).child(requestId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pax = snapshot.child("pax").value.toString()
                    val doneeName = snapshot.child("doneeName").value.toString()
                    tvPax.text = "Fed $pax people of $doneeName"
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        fun loadDonorImage(context: Context, donorId: String, ivDonor: ImageView) {
            val ref = Firebase.database.getReference("Users").child("Donor")
            ref.child(donorId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val donorImage = snapshot.child("profileImage").value.toString()
                        if (donorImage != "") {
                            Picasso.with(context).load(donorImage).into(ivDonor)
                        } else {
                            ivDonor.setImageResource(R.drawable.placeholder)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun generateRandomString(): String {
            val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
            return (1..6)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }
    }
}