package rsd.mad.mykasihv1.ui.history

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentMyRewardBinding
import rsd.mad.mykasihv1.models.Redemption
import rsd.mad.mykasihv1.room.RedemptionViewModel
import java.text.NumberFormat
import java.util.*

class MyRewardFragment : Fragment() {

    private lateinit var binding: FragmentMyRewardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private val myRedemptionViewModel : RedemptionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        with(binding) {
            lblReward.text = NumberFormat.getNumberInstance(Locale.US).format(sharedPref.getInt(getString(R.string.point), 0))

            btnRedeem1.setOnClickListener {
                redeem(500000,getString(R.string.voucher_1))
            }

            btnRedeem2.setOnClickListener {
                redeem(100000,getString(R.string.voucher_2))
            }

            btnRedeem3.setOnClickListener {
                redeem(10000,getString(R.string.voucher_3))
            }
        }
    }

    private fun redemption(voucher: String, points: Int) {
        val timestamp = System.currentTimeMillis()

        val database = Firebase.database
        val ref = database.getReference("Redemption").push()
        val key = ref.key.toString()
        val redemption = Redemption(key, auth.uid!!, voucher, points, timestamp)

        myRedemptionViewModel.addRedemption(redemption)
        database.getReference("Redemption").child(key).setValue(redemption)
    }

    private fun redeem(point: Int, voucher: String) {
        auth = Firebase.auth
        var points = 0
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage("Are you sure you want to claim?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                if (sharedPref.getInt(getString(R.string.point), 0) >= point) {
                    points = sharedPref.getInt(getString(R.string.point), 0) - point

                    val role = sharedPref.getString(getString(R.string.role), "")

                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["point"] = points

                    Firebase.database.getReference("Users").child(role!!).child(auth.uid!!)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            redemption(voucher, point)
                            toast("Voucher redeemed successfully")
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener {
                            toast("Voucher failed to redeem")
                        }
                } else {
                    toast("Insufficient points")
                }
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
}