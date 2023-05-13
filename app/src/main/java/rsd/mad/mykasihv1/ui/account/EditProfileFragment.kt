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
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentEditProfileBinding

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
    private var city = ""
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
            city = spCityEdit.selectedItem as String
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

            val phoneRegex = Regex("^01\\d-\\d{7,8}$")
            if (mobile.isEmpty()) {
                edtMobile.error = getString(R.string.err_empty)
                isValid = false
            } else if (!phoneRegex.matches(mobile)) {
                edtMobile.error = getString(R.string.err_mobile)
                isValid = false
            }

            if (isValid) {
                if (currentPass.isNotEmpty() && newPass.isNotEmpty()) {
                    val passwordRegex = Regex(".{6,}")
                    if (currentPass == newPass) {
                        edtNewPass.error = getString(R.string.err_same_password)
                        return
                    } else if (passwordRegex.matches(newPass)) {
                        edtNewPass.error = getString(R.string.err_password)
                        return
                    } else {
                        changePassword()
                    }
                }
                if (imageUri == null) {
                    updateProfile("")
                } else {
                    uploadImage()
                }
            }
        }
    }

    private fun changePassword() {
        val firebaseUser = auth.currentUser
        val credential: AuthCredential =
            EmailAuthProvider.getCredential(firebaseUser!!.email!!, currentPass)
        firebaseUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    firebaseUser.updatePassword(newPass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            toast("Password updated")

                            // it is signing out the user from the current status once changing password is successful
                            // it is changing the activity and going to the sign in page while clearing the backstack so the user cant come to the current state by back pressing

                            auth.signOut()
                            val i = Intent(activity, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(i)
                            (activity as Activity?)!!.overridePendingTransition(0, 0)
                        } else
                            toast("Error password not updated")
                    }
                }
                else -> {
                    toast("Incorrect old password")
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
        auth.signInWithEmailAndPassword(
            firebaseUser!!.email!!,
            sharedPref.getString(getString(R.string.password), "")!!
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser!!.updateEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
//                                toast("Email updated successfully")
                                val role = sharedPref.getString(getString(R.string.role), "")

                                val hashMap: HashMap<String, Any> = HashMap()
                                hashMap["name"] = "$name"
                                hashMap["email"] = "$email"
                                hashMap["mobile"] = "$mobile"
                                hashMap["address"] = "$address"
                                hashMap["city"] = "$city"
                                if (imageUri != null)
                                    hashMap["profileImage"] = uploadedImageUrl

                                database.getReference("Users").child(role!!).child(auth.uid!!)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener {
                                        toast("Profile updated")
                                        findNavController().popBackStack()
                                    }
                                    .addOnFailureListener {
                                        toast("Failed to update profile due to ${it.message}")
                                    }
                            } else
//                                toast("Failed to update email")
                                when (task.exception) {
                                    is FirebaseAuthUserCollisionException -> toast("This email is already in use")
                                    is FirebaseAuthInvalidCredentialsException -> toast("Please enter a valid email")
                                    else -> toast("Error")
                                }
                        }
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> toast("This email is already in use")
                        is FirebaseAuthInvalidCredentialsException -> toast("Please enter a valid email")
                        else -> toast("Error")
                    }
                }
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
                binding.edtEmail.setText(getString(getString(R.string.email), ""))

                val index = getSpinnerItemIndex(binding.spCityEdit, "${getString(getString(R.string.city), "")}")
                if (index != -1) {
                    binding.spCityEdit.setSelection(index)
                } else {
                    binding.spCityEdit.setSelection(0)
                }

                var profileImage = sharedPref.getString(getString(R.string.profileImage), "")
                if (profileImage == "")
                    binding.ivProfile.setImageResource(R.drawable.empty)
                else {
                    Picasso.with(context).load(profileImage)
                        .placeholder(R.drawable.progress_animation).error(R.drawable.try_later)
                        .into(binding.ivProfile)
                }
            }
        }
    }

    private fun getSpinnerItemIndex(spinner: Spinner, itemName: String): Int {
        for (i in 0 until spinner.adapter.count) {
            val item = spinner.adapter.getItem(i).toString()
            if (item == itemName) {
                return i
            }
        }
        return -1
    }
}