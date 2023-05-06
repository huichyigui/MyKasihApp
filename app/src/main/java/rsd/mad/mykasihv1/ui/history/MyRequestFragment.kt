package rsd.mad.mykasihv1.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
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

        showData("")

        with(binding) {
            btnSortRequest.setOnClickListener { showSortDialog() }
        }
    }

    private fun showData(s: String) {
        val ref = Firebase.database.getReference("RequestDonation").child(auth.uid!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestsArrayList.clear()
                for (request in snapshot.children) {
                    val model = request.getValue(RequestDonation::class.java)
                    requestsArrayList.add(model!!)
                }
                if (requestsArrayList.isNotEmpty()) {
                    if (s == "DESCENDING")
                        requestsArrayList.reverse()
                    requestDonationList = RequestDonationList(requireActivity(), requestsArrayList)
                    binding.rvRequests.adapter = requestDonationList
                    binding.tvViewCountRequest.isVisible = false
                    binding.btnSortRequest.isVisible = true
                } else {
                    binding.tvViewCountRequest.text = getString(R.string.no_record)
                    binding.tvViewCountRequest.isVisible = true
                    binding.btnSortRequest.isVisible = false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showSortDialog() {
        val sort = resources.getStringArray(R.array.sort)

        val popupMenu = PopupMenu(context, binding.btnSortRequest)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Earliest to latest")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Latest to earliest")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> {
                    showData("ASCENDING")
                    binding.btnSortRequest.text = sort[0]
                }
                1 -> {
                    showData("DESCENDING")
                    binding.btnSortRequest.text = sort[1]
                }
            }
            true
        }
    }
}