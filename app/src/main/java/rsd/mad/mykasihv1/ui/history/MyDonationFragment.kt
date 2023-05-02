package rsd.mad.mykasihv1.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                donationList = DonationList(requireActivity(), donationArrayList)
                binding.rvDonation.adapter = donationList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}