package rsd.mad.mykasihv1.ui.home

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentDonationDetailBinding
import rsd.mad.mykasihv1.fcm.NotificationData
import rsd.mad.mykasihv1.fcm.PushNotification
import rsd.mad.mykasihv1.fcm.RetrofitInstance
import rsd.mad.mykasihv1.models.Donation

class DonationDetailFragment : Fragment() {

    private lateinit var binding: FragmentDonationDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var database = Firebase.database
    private var imageUri: Uri? = null
    private var pointEarned = 0

    val TAG = "DonationDetailActivity"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val args = NavHostFragment.findNavController(this).currentBackStackEntry?.arguments
        val donor = args?.getSerializable("donor") as? Donation

        with(binding) {
            val rowDonation = TableRow(context)
            val colDonation1 = TextView(context)
            colDonation1.text = donor!!.foodCategory
            rowDonation.addView(colDonation1)

            val colDonation2 = TextView(context)
            colDonation2.text = donor!!.amount
            rowDonation.addView(colDonation2)
            tblDonationInfo.addView(rowDonation)

            val fieldName = arrayOf(
                "Date",
                "Time",
                "Location"
            )
            val fieldValue = arrayOf(
                donor!!.date,
                donor!!.time,
                donor!!.location
            )

            val tblRow = TableRow(context)
            val colFirstPickup = TextView(context)
            colFirstPickup.text = "Donor Name"
            tblRow.addView(colFirstPickup)

            val colFirstPickupValue = TextView(context)
            Helper.loadDonorName(donor!!.donorId, colFirstPickupValue)
            tblRow.addView(colFirstPickupValue)
            tblPickupInfo.addView(tblRow)

            for (i in fieldName.indices) {
                val tableRow = TableRow(context)

                val tvFieldName = TextView(context)
                tvFieldName.text = fieldName[i]
                tableRow.addView(tvFieldName)

                val tvFieldValue = TextView(context)
                tvFieldValue.text = fieldValue[i]
                tableRow.addView(tvFieldValue)

                tblPickupInfo.addView(tableRow)
            }

            btnUploadProof.setOnClickListener { pickImageCamera() }
            btnClaimDonation.setOnClickListener { validateData() }
        }
    }


    private fun validateData() {
        val args = NavHostFragment.findNavController(this).currentBackStackEntry?.arguments
        val donor = args?.getSerializable("donor") as? Donation
        val actualToken = donor!!.token
        val token = binding.edtToken.text.toString().trim()

        var isValid = true

        if (token.isEmpty()) {
            binding.edtToken.error = getString(R.string.err_empty)
            isValid = false
        } else if (token != actualToken) {
            binding.edtToken.error = getString(R.string.err_token)
            isValid = false
        }

        if (imageUri == null) {
            toast("Missing delivery proof image")
            isValid = false
        }

        if (isValid) {
            uploadImage()
        }
    }

    private fun calPoints(amount:Int):Int{
        var points = 0

        when (amount) {
            in 1 .. 49 -> points = 100
            in 50 .. 100 -> points = 1000
            in 101 .. 200 -> points = 5000
            in 201 .. 500 -> points = 8000
            else -> points = 10000
        }

        return points
    }

    private fun claimDonation(uploadedImageUrl: String) {
        val args = NavHostFragment.findNavController(this).currentBackStackEntry?.arguments
        val donor = args?.getSerializable("donor") as? Donation

        retrieveDeviceToken(donor!!)

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["status"] = "Received"
        hashMap["proofImage"] = uploadedImageUrl

        var points = donor.amount
        points = points.replace(("[^\\d.]").toRegex(), "")
        pointEarned = calPoints(points.toInt())

        val ref = database.getReference("Donation")
        ref.orderByChild("token").equalTo(donor!!.token).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (donation in snapshot.children) {
                    donation.ref.updateChildren(hashMap)

                    val donorRef = database.getReference("Users").child("Donor").child(donor.donorId)
                    donorRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val currentPoint = dataSnapshot.child("point").value as Long
                            donorRef.child("point").setValue(currentPoint + pointEarned)

                            retrieveDeviceToken(donor)

                            toast("Claimed successfully")
                            findNavController().popBackStack()
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun uploadImage() {
        val timestamp = System.currentTimeMillis()

        val filePathAndName = "DonationProof/$timestamp"
        val ref = Firebase.storage.getReference(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                claimDonation(uploadedImageUrl)
            }
            .addOnFailureListener {
                toast("ERROR: Failed to upload image due to ${it.message}")
            }
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                binding.ivProof.setImageURI(imageUri)
            } else {
                toast("Cancelled")
            }
        }
    )

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun retrieveDeviceToken(donation: Donation) {
        val ref = database.getReference("Users").child("Donor").child(donation.donorId).child("device")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val device = snapshot.value.toString()

                val doneeName = sharedPref.getString(getString(R.string.name), "")
                val recipientToken = sharedPref.getString(getString(R.string.device_token), "")
                val title = "Thanks for your donation!"
                val message = "$doneeName received ${donation.amount} of ${donation.foodCategory} from you!."

                if (recipientToken!!.isNotEmpty()) {
                    PushNotification(
                        NotificationData(title, message),
                        device
                    ).also {
                        sendNotification(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}