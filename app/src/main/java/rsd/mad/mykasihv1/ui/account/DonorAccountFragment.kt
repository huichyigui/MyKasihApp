package rsd.mad.mykasihv1.ui.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentDonorAccountBinding

class DonorAccountFragment : Fragment() {

    private lateinit var binding: FragmentDonorAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        with(binding) {
            lblDonorName.text = sharedPref.getString(getString(R.string.name), "")
            lblDonorEmail.text = "${auth.currentUser!!.email}"
            btnEdit.setOnClickListener { findNavController().navigate(R.id.action_nav_donor_account_to_nav_donor_edit_profile) }
            btnLogout.setOnClickListener {
                auth.signOut()
                startActivity(Intent(context, MainActivity::class.java))
            }
        }
    }
}