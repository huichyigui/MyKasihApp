package rsd.mad.mykasihv1.ui.requestDonation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentRequestDonationBinding

class RequestDonationFragment : Fragment() {

    private lateinit var binding: FragmentRequestDonationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        with(binding) {
            edtDoneeName.setText(sharedPref.getString(getString(R.string.name), ""))
        }
    }
}