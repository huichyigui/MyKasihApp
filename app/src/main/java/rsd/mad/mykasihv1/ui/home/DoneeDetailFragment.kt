package rsd.mad.mykasihv1.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.DonateFoodActivity
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentDoneeDetailBinding
import rsd.mad.mykasihv1.models.RequestDonation

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

        val args = NavHostFragment.findNavController(this).currentBackStackEntry?.arguments
        val donee = args?.getSerializable("donee") as? RequestDonation

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = donee!!.doneeName

        with(binding) {
            Picasso.with(context).load(donee!!.orgImage).placeholder(R.drawable.progress_animation).error(R.drawable.try_later).into(ivDoneeOrgImage)
            tvDoneeDescription.text = donee!!.description
            tvPax.text = "Pax: ${donee!!.pax}"
            btnDonateDonor.setOnClickListener {
                var intent = Intent(context, DonateFoodActivity::class.java)
                intent.putExtra(getString(R.string.donee_id), donee!!.doneeId)
                intent.putExtra(getString(R.string.timestamp), donee!!.timestamp)
                startActivity(intent)
            }
        }
    }
}