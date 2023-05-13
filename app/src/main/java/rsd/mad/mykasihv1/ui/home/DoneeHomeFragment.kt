package rsd.mad.mykasihv1.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.MainActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.adapter.DonorList
import rsd.mad.mykasihv1.databinding.FragmentDoneeHomeBinding
import rsd.mad.mykasihv1.models.Donation
import rsd.mad.mykasihv1.ui.requestDonation.RequestDonationFragment

class DoneeHomeFragment : Fragment() {

    private lateinit var binding: FragmentDoneeHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    private lateinit var donorArrayList: ArrayList<Donation>
    private lateinit var donorList: DonorList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoneeHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (auth.currentUser == null) {
            startActivity(Intent(context, MainActivity::class.java))
            activity?.fragmentManager?.popBackStack()
        } else {
            with(sharedPref) {
                binding.lblWelcomeDonee.text = "Welcome, ${getString(getString(R.string.name), "")}"
                binding.lblDoneeAddress.text = getString(getString(R.string.city), "")
            }
        }

        with(binding) {
            fab.setOnClickListener { findNavController().navigate(R.id.action_nav_donee_home_to_nav_request_donation) }
        }

        donorArrayList = ArrayList()

        val ref = Firebase.database.getReference("Donation")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (donation in snapshot.children) {
                    if (donation.child("status").value == "Pending") {
                        val doneeId = donation.child("doneeId").value.toString()
                        if (doneeId == auth.uid) {
                            val model = donation.getValue(Donation::class.java)
                            donorArrayList.add(model!!)
                        }
                    }
                }
                if (donorArrayList.isNotEmpty()) {
                    donorArrayList.sortByDescending { it.timestamp }
                    donorList = DonorList(requireActivity(), donorArrayList)
                    binding.rvDonor.adapter = donorList
                    binding.tvViewCountDonee.isVisible = false
                } else {
                    binding.tvViewCountDonee.text = getString(R.string.no_record)
                    binding.tvViewCountDonee.isVisible = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}