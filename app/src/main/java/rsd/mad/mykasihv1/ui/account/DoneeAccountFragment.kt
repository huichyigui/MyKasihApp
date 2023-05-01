package rsd.mad.mykasihv1.ui.account

import android.app.Activity
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
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentDoneeAccountBinding

class DoneeAccountFragment : Fragment() {

    private lateinit var binding: FragmentDoneeAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoneeAccountBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        with(binding) {
            var profileImage = sharedPref.getString(getString(R.string.profileImage), "")
            if (profileImage == "")
                ivDoneeProfile.setImageResource(R.drawable.empty)
            else {
                Picasso.with(context).load(profileImage).into(ivDoneeProfile)
            }

            lblDoneeName.text = sharedPref.getString(getString(R.string.name), "")
            lblDoneeEmail.text = sharedPref.getString(getString(R.string.email), "")

            btnEdit.setOnClickListener { findNavController().navigate(R.id.action_nav_donee_account_to_nav_donee_edit_profile) }
            btnLogout.setOnClickListener {
                auth.signOut()

                val i = Intent(activity, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
            btnMyRequest.setOnClickListener { findNavController().navigate(R.id.action_nav_donee_account_to_nav_my_request) }
            btnMyClaim.setOnClickListener { findNavController().navigate(R.id.action_nav_donee_account_to_nav_my_claim) }
        }
    }
}