package rsd.mad.mykasihv1.ui.requestDonation

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentRequestDonationBinding
import rsd.mad.mykasihv1.models.RequestDonation
import rsd.mad.mykasihv1.ui.home.DoneeHomeFragment

class RequestDonationFragment : Fragment() {

    private lateinit var binding: FragmentRequestDonationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null
    private var database = Firebase.database
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        with(binding) {
            edtDoneeName.setText(sharedPref.getString(getString(R.string.name), ""))
            btnUploadOrgImage.setOnClickListener { showImageAttach() }
            btnSubmitRequestDonation.setOnClickListener { validateData() }
        }
    }

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    private var name = ""
    private var description = ""
    private var pax = 0
    private var orgImage = ""
    private fun validateData() {
        var isValid = true

        with(binding) {
            name = edtDoneeName.text.toString().trim()
            description = edtDescription.text.toString().trim()
            pax = edtPax.text.toString().toIntOrNull() ?: 0

            if (name.isEmpty()) {
                edtDoneeName.error = getString(R.string.err_empty)
                isValid = false
            }

            if (description.isEmpty()) {
                edtDescription.error = getString(R.string.err_empty)
                isValid = false
            }

            if (imageUri == null) {
                toast("Missing organization image")
                isValid = false
            }

            if (pax <= 0) {
                edtPax.error = getString(R.string.err_pax)
                isValid = false
            }

            if (isValid) {
                requestDonation()
            }
        }
    }

    private fun requestDonation() {
        progressDialog.setMessage("Saving request info")

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "RequestDonation/$timestamp"
        var ref = Firebase.storage.getReference(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                val requestDonation =
                    RequestDonation(auth.uid!!, name, description, pax, uploadedImageUrl, timestamp)

                database.getReference("RequestDonation").child(auth.uid!!).child("$timestamp")
                    .setValue(requestDonation)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        toast("Request added")
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        toast("Database error")
                    }
            }
            .addOnFailureListener {
                toast("ERROR: Failed to upload image due to ${it.message}")
            }
    }

    private fun showImageAttach() {
        val popupMenu = PopupMenu(context, binding.btnUploadOrgImage)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> pickImageCamera()
                1 -> pickImageGallery()
            }
            true
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data

                binding.ivDoneePreview.setImageURI(imageUri)
            } else {
                toast("Cancelled")
            }
        }
    )

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
//                imageUri = data!!.data

                binding.ivDoneePreview.setImageURI(imageUri)
            } else {
                toast("Cancelled")
            }
        }
    )
}