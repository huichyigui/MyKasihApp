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
import rsd.mad.mykasihv1.adapter.RedemptionList
import rsd.mad.mykasihv1.databinding.FragmentMyRedeemBinding
import rsd.mad.mykasihv1.models.Redemption

class MyRedeemFragment : Fragment() {

    private lateinit var binding: FragmentMyRedeemBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var redemptionArrayList: ArrayList<Redemption>
    private lateinit var redemptionList: RedemptionList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRedeemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        redemptionArrayList = ArrayList()

        var ref = Firebase.database.getReference("Redemption")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                redemptionArrayList.clear()
                for (redemption in snapshot.children) {
                    if (redemption.child("donorId").value == auth.currentUser?.uid) {
                        val model = redemption.getValue(Redemption::class.java)
                        redemptionArrayList.add(model!!)
                    }
                }
                redemptionList = RedemptionList(requireActivity(), redemptionArrayList)
                binding.rvRedemption.adapter = redemptionList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}