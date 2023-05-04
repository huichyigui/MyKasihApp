package rsd.mad.mykasihv1.ui.history

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentMyRewardBinding
import rsd.mad.mykasihv1.models.Redemption

class MyRewardFragment : Fragment() {

    private lateinit var binding: FragmentMyRewardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        var points = 0

        with(binding) {
            lblReward.text = "${sharedPref.getInt(getString(R.string.point), 0)}"

            btnRedeem1.setOnClickListener {
                if (sharedPref.getInt(getString(R.string.point), 0) >= 500000) {
                    points = sharedPref.getInt(getString(R.string.point), 0) - 500000

                    val role = sharedPref.getString(getString(R.string.role), "")

                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["point"] = "$points"

                    Firebase.database.getReference("Users").child(role!!).child(auth.uid!!)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            redemption(getString(R.string.voucher_1), "500000")
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

            btnRedeem2.setOnClickListener {
                if (sharedPref.getInt(getString(R.string.point), 0) >= 100000) {
                    points = sharedPref.getInt(getString(R.string.point), 0) - 100000

                    val role = sharedPref.getString(getString(R.string.role), "")

                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["point"] = "$points"

                    Firebase.database.getReference("Users").child(role!!).child(auth.uid!!)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            redemption(getString(R.string.voucher_2), "100000")
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

            btnRedeem3.setOnClickListener {
                if (sharedPref.getInt(getString(R.string.point), 0) >= 10000) {
                    points = sharedPref.getInt(getString(R.string.point), 0) - 10000

                    val role = sharedPref.getString(getString(R.string.role), "")

                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["point"] = "$points"

                    Firebase.database.getReference("Users").child(role!!).child(auth.uid!!)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            redemption(getString(R.string.voucher_3), "10000")
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
        }
    }

    private fun redemption(voucher: String, points: String) {
        val timestamp = System.currentTimeMillis()

        val database = Firebase.database
        val redemption = Redemption(auth.uid!!, voucher, points, timestamp)

        var ref = database.getReference("Redemption").push()
        ref.setValue(redemption)
    }

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
}