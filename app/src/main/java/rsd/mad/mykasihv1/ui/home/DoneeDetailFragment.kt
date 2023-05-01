package rsd.mad.mykasihv1.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentDoneeDetailBinding

class DoneeDetailFragment : Fragment() {

    private lateinit var binding: FragmentDoneeDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoneeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
//        actionBar?.title = "${donee.doneeName}"
    }
}