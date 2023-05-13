package rsd.mad.mykasihv1.ui.community

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.adapter.ClaimList
import rsd.mad.mykasihv1.adapter.CommunityList
import rsd.mad.mykasihv1.databinding.FragmentCommunityBinding
import rsd.mad.mykasihv1.models.Donation

class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private var role = ""

    private lateinit var communityArrayList: ArrayList<Donation>
    private lateinit var communityList: CommunityList
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
        communityArrayList = ArrayList()

        loadUserInfo()

        with(binding) {
            role = sharedPref.getString(getString(R.string.role), "")!!

            if (role == getString(R.string.donor)) {

                auth = Firebase.auth

                val ref = Firebase.database.getReference("Users").child("Donor")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (donorSnapshot in snapshot.children) {
                            if (donorSnapshot.key == auth.uid!!) {
                                val point = donorSnapshot.child("point").value
                                lblPointCommunity.text = String.format("%,d points", point)
                                sharedPref.edit().putInt(getString(R.string.point), point.toString().toInt()).apply()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
                btnLeaderboard.setOnClickListener { findNavController().navigate(R.id.action_nav_donor_community_to_nav_donor_leaderboard) }
            } else {
                lblPointCommunity.visibility = View.GONE
                btnLeaderboard.setOnClickListener { findNavController().navigate(R.id.action_nav_donee_community_to_nav_donee_leaderboard) }
            }
        }

        binding.progressBar.visibility = View.VISIBLE
        val ref = Firebase.database.getReference("Donation")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                communityArrayList.clear()
                for (post in snapshot.children) {
                    if (post.child("status").value == "Received") {
                        val model = post.getValue(Donation::class.java)
                        communityArrayList.add(model!!)
                    }
                }

                binding.progressBar.visibility = View.GONE
                // Ensure the fragment is attached to an activity before calling requireActivity()
                activity?.let {
                    if (communityArrayList.isNotEmpty()) {
                        communityList = CommunityList(requireActivity(), communityArrayList)
                        binding.rvCommunity.adapter = communityList
                        binding.tvViewCountCommunity.isVisible = false
                    } else {
                        binding.tvViewCountCommunity.text = getString(R.string.no_record)
                        binding.tvViewCountCommunity.isVisible = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var activity: AppCompatActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as? AppCompatActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
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
                    Picasso.with(context).load(profileImage).placeholder(R.drawable.progress_animation).error(R.drawable.try_later).into(binding.ivProfileCommunity)
                }
            }
        }
    }
}