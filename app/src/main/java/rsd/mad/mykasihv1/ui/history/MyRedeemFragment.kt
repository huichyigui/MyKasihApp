package rsd.mad.mykasihv1.ui.history

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.R
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

        showData("DESCENDING")

        with(binding) {
            btnSortRedeem.setOnClickListener { showSortDialog() }
        }
    }

    private fun showData(s: String) {
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

                // Ensure the fragment is attached to an activity before calling requireActivity()
                activity?.let {
                    if (redemptionArrayList.isNotEmpty()) {
                        if (s == "DESCENDING")
                            redemptionArrayList.reverse()
                        redemptionList = RedemptionList(requireActivity(), redemptionArrayList)
                        binding.rvRedemption.adapter = redemptionList
                        binding.tvViewCountRedeem.isVisible = false
                        binding.btnSortRedeem.isVisible = true
                    } else {
                        binding.tvViewCountRedeem.text = getString(R.string.no_record)
                        binding.tvViewCountRedeem.isVisible = true
                        binding.btnSortRedeem.isVisible = false
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

        val popupMenu = PopupMenu(context, binding.btnSortRedeem)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Earliest to latest")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Latest to earliest")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> {
                    showData("ASCENDING")
                    binding.btnSortRedeem.text = sort[0]
                }
                1 -> {
                    showData("DESCENDING")
                    binding.btnSortRedeem.text = sort[1]
                }
            }
            true
        }
    }

}