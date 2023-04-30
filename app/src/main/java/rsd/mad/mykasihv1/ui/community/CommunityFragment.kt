package rsd.mad.mykasihv1.ui.community

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
import rsd.mad.mykasihv1.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var role = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentCommunityBinding.inflate(inflater, container, false)
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
            role = sharedPref.getString(getString(R.string.role), "")!!
            if (role == getString(R.string.donor))
                btnLeaderboard.setOnClickListener { findNavController().navigate(R.id.action_nav_donor_community_to_nav_donor_leaderboard) }
            else
                btnLeaderboard.setOnClickListener { findNavController().navigate(R.id.action_nav_donee_community_to_nav_donee_leaderboard) }
        }
    }

    private fun loadUserInfo() {
        var firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(context, MainActivity::class.java))
        } else {
            with(sharedPref) {
                binding.lblNameCommunity.text = getString(getString(R.string.name), "")

                if (role == getString(R.string.donor))
                    binding.lblPointCommunity.text = "${sharedPref.getInt(getString(R.string.point), 0)} points"
                else
                    binding.lblPointCommunity.text = ""

                var profileImage = sharedPref.getString(getString(R.string.profileImage), "")
                if (profileImage == "")
                    binding.ivProfileCommunity.setImageResource(R.drawable.empty)
                else {
                    Picasso.with(context).load(profileImage).into(binding.ivProfileCommunity)
                }
            }
        }
    }
}