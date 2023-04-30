package rsd.mad.mykasihv1.ui.account

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentEditProfileBinding
import rsd.mad.mykasihv1.models.User

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var database = Firebase.database
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        loadUserInfo()

        with(binding) {
            btnEdit.setOnClickListener { edit() }
            btnUploadProfile.setOnClickListener { showImageAttach() }
        }
    }

    private var name = ""
    private var email = ""
    private var mobile = ""
    private var address = ""
    private var currentPass = ""
    private var newPass = ""

    private fun edit() {
        var isValid = true

        // Get Data
        with(binding) {
            name = edtName.text.toString().trim()
            email = edtEmail.text.toString().trim()
            mobile = edtMobile.text.toString().trim()
            address = edtAddress.text.toString().trim()
            currentPass = edtCurrentPass.text.toString()
            newPass = edtNewPass.text.toString()

            if (name.isEmpty()) {
                edtName.error = getString(R.string.err_empty)
                isValid = false
            }

            if (email.isEmpty()) {
                edtEmail.error = getString(R.string.err_empty)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = getString(R.string.err_email)
                isValid = false
            }

            if (mobile.isEmpty()) {
                edtMobile.error = getString(R.string.err_empty)
                isValid = false
            } else if (!Patterns.PHONE.matcher(mobile).matches()) {
                edtMobile.error = getString(R.string.err_mobile)
                isValid = false
            }

            if (isValid) {
                if (currentPass.isNotEmpty() && newPass.isNotEmpty()) {
//                    changePassword()
                }
                if (imageUri == null) {
                    updateProfile("")
                } else {
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage() {
        val filePathAndName = "ProfileImages/" + auth.uid
        var ref = Firebase.storage.getReference(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                updateProfile(uploadedImageUrl)
            }
            .addOnFailureListener {
                toast("ERROR: Failed to upload image due to ${it.message}")
            }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        val firebaseUser = auth.currentUser
        auth.signInWithEmailAndPassword(firebaseUser!!.email!!,
            sharedPref.getString(getString(R.string.password), "")!!
        )
            .addOnCompleteListener {  task->
                if (task.isSuccessful) {
                    auth.currentUser!!.updateEmail(email)
                        .addOnCompleteListener { task->
                            if (task.isSuccessful)
                                toast("Email updated successfully")
                            else
                                toast("Failed to update email")
                        }
                } else {
                    toast("Sign in Error")
                }
            }

        val role = sharedPref.getString(getString(R.string.role), "")

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["name"] = "$name"
        hashMap["email"] = "$email"
        hashMap["mobile"] = "$mobile"
        hashMap["address"] = "$address"
        if (imageUri != null)
            hashMap["profileImage"] = uploadedImageUrl

        database.getReference("Users").child(role!!).child(auth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                toast("Profile updated")
            }
            .addOnFailureListener {
                toast("Failed to update profile due to ${it.message}")
            }
    }

    private fun showImageAttach() {
        val popupMenu = PopupMenu(context, binding.btnUploadProfile)
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

                binding.ivProfile.setImageURI(imageUri)
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

                binding.ivProfile.setImageURI(imageUri)
            } else {
                toast("Cancelled")
            }
        }
    )

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    private fun loadUserInfo() {
        var firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(context, MainActivity::class.java))
        } else {
            with(sharedPref) {
                binding.edtName.setText(getString(getString(R.string.name), ""))
                binding.edtAddress.setText(getString(getString(R.string.address), ""))
                binding.edtMobile.setText(getString(getString(R.string.mobile), ""))
                binding.edtEmail.setText(firebaseUser.email)
            }
        }
    }
}