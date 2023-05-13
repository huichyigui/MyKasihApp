package rsd.mad.mykasihv1.ui.home

import android.app.SearchManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.adapter.DoneeList
import rsd.mad.mykasihv1.databinding.FragmentDonorHomeBinding
import rsd.mad.mykasihv1.models.RequestDonation


class DonorHomeFragment : Fragment() {

    private lateinit var binding: FragmentDonorHomeBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    private lateinit var doneeArrayList: ArrayList<RequestDonation>
    private lateinit var doneeList: DoneeList

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        with(sharedPref) {
            binding.lblWelcomeDonor.text = "Welcome, ${getString(getString(R.string.name), "")}"
            binding.lblAddressDonor.text = getString(getString(R.string.city), "")
        }

        doneeArrayList = ArrayList()

        val city = sharedPref.getString(getString(R.string.city), "")

        binding.progressBar2.visibility = View.VISIBLE

        val ref = Firebase.database.getReference("RequestDonation")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (doneeIdSnapshot in snapshot.children) {
                    for (requestIdSnapshot in doneeIdSnapshot.children) {
                        if (requestIdSnapshot.child("city").value.toString() == city &&
                            requestIdSnapshot.child("status").value.toString() == getString(R.string.active)) {
                                val model = requestIdSnapshot.getValue(RequestDonation::class.java)
                                doneeArrayList.add(model!!)
                        }
                    }
                }
                binding.progressBar2.visibility = View.GONE
                doneeList = DoneeList(requireActivity(), doneeArrayList)
                binding.rvDonee.adapter = doneeList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    doneeList.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
}