package rsd.mad.mykasihv1.ui.history

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.adapter.DonationList
import rsd.mad.mykasihv1.databinding.FragmentMyDonationBinding
import rsd.mad.mykasihv1.models.Donation
import rsd.mad.mykasihv1.models.RequestDonation

class MyDonationFragment : Fragment() {

    private lateinit var binding: FragmentMyDonationBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var donationArrayList: ArrayList<Donation>
    private lateinit var donationList: DonationList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        donationArrayList = ArrayList()

        showData("DESCENDING")

        with(binding) {
            btnSortDonation.setOnClickListener { showSortDialog() }
            cpPending.setOnClickListener { showDonationBasedOnStatus("Pending") }
            cpReceived.setOnClickListener { showDonationBasedOnStatus("Received") }
            cpAllDonation.setOnClickListener { showData("DESCENDING") }
        }
    }

    private fun showDonationBasedOnStatus(status: String) {
        binding.progressBar4.visibility = View.VISIBLE
        var ref = Firebase.database.getReference("Donation")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donationArrayList.clear()
                for (donation in snapshot.children) {
                    if (donation.child("donorId").value == auth.currentUser?.uid && donation.child("status").value == status) {
                        val model = donation.getValue(Donation::class.java)
                        donationArrayList.add(model!!)
                    }
                }
                binding.progressBar4.visibility = View.GONE
                binding.cgLegends.visibility = View.VISIBLE
                binding.btnSortDonation.visibility = View.VISIBLE

                // Ensure the fragment is attached to an activity before calling requireActivity()
                activity?.let {
                    if (donationArrayList.isNotEmpty()) {
                        binding.rvDonation.visibility = View.VISIBLE
                        donationArrayList.reverse()
                        donationList = DonationList(requireActivity(), donationArrayList)
                        binding.rvDonation.adapter = donationList
                        binding.tvViewCount.visibility = View.GONE
                    } else {
                        binding.rvDonation.visibility = View.GONE
                        binding.tvViewCount.text = getString(R.string.no_record)
                        binding.tvViewCount.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showData(s: String) {
        binding.progressBar4.visibility = View.VISIBLE
        var ref = Firebase.database.getReference("Donation")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donationArrayList.clear()
                for (donation in snapshot.children) {
                    if (donation.child("donorId").value == auth.currentUser?.uid) {
                        val model = donation.getValue(Donation::class.java)
                        donationArrayList.add(model!!)
                    }
                }
                binding.progressBar4.visibility = View.GONE
                // Ensure the fragment is attached to an activity before calling requireActivity()
                activity?.let {
                    if (donationArrayList.isNotEmpty()) {
                        binding.rvDonation.visibility = View.VISIBLE
                        if (s == "DESCENDING")
                            donationArrayList.reverse()
                        donationList = DonationList(requireActivity(), donationArrayList)
                        binding.rvDonation.adapter = donationList
                        binding.tvViewCount.visibility = View.GONE
                        binding.cgLegends.visibility = View.VISIBLE
                        binding.btnSortDonation.visibility = View.VISIBLE
                    } else {
                        binding.rvDonation.visibility = View.GONE
                        binding.tvViewCount.text = getString(R.string.no_record)
                        binding.tvViewCount.visibility = View.VISIBLE
                        binding.btnSortDonation.visibility = View.GONE
                        binding.cgLegends.visibility = View.GONE
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

    private fun showSortDialog() {
        val sort = resources.getStringArray(R.array.sort)

        val popupMenu = PopupMenu(context, binding.btnSortDonation)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Earliest to latest")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Latest to earliest")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> {
                    showData("ASCENDING")
                    binding.btnSortDonation.text = sort[0]
                }
                1 -> {
                    showData("DESCENDING")
                    binding.btnSortDonation.text = sort[1]
                }
            }
            true
        }
    }
}