package rsd.mad.mykasihv1.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.adapter.ClaimList
import rsd.mad.mykasihv1.databinding.FragmentMyClaimBinding
import rsd.mad.mykasihv1.models.Donation

class MyClaimFragment : Fragment() {

    private lateinit var binding: FragmentMyClaimBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var claimArrayList: ArrayList<Donation>
    private lateinit var claimList: ClaimList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyClaimBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        claimArrayList = ArrayList()

        val ref = Firebase.database.getReference("Donation")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                claimArrayList.clear()
                for (claim in snapshot.children) {
                    if (claim.child("doneeId").value == auth.uid) {
                        if (claim.child("status").value == "Received") {
                            val model = claim.getValue(Donation::class.java)
                            claimArrayList.add(model!!)
                        }
                    }
                }
                claimArrayList.reverse()
                claimList = ClaimList(requireActivity(), claimArrayList)
                binding.rvMyClaim.adapter = claimList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}