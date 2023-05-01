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
import rsd.mad.mykasihv1.adapter.RequestDonationList
import rsd.mad.mykasihv1.databinding.FragmentMyRequestBinding
import rsd.mad.mykasihv1.models.RequestDonation

class MyRequestFragment : Fragment() {

    private lateinit var binding: FragmentMyRequestBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var requestsArrayList : ArrayList<RequestDonation>
    private lateinit var requestDonationList: RequestDonationList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        requestsArrayList = ArrayList()

        val ref = Firebase.database.getReference("RequestDonation").child(auth.uid!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestsArrayList.clear()
                for (request in snapshot.children) {
                    val model = request.getValue(RequestDonation::class.java)
                    requestsArrayList.add(model!!)
                }
                requestDonationList = RequestDonationList(requireActivity(), requestsArrayList)
                binding.rvRequests.adapter = requestDonationList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}