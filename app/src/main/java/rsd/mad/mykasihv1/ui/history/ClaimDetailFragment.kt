package rsd.mad.mykasihv1.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentClaimDetailBinding
import rsd.mad.mykasihv1.models.Donation

class ClaimDetailFragment : Fragment() {

    private lateinit var binding: FragmentClaimDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentClaimDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = NavHostFragment.findNavController(this).currentBackStackEntry?.arguments
        val donor = args?.getSerializable("claim") as? Donation

        with(binding) {
            val rowDonation = TableRow(context)
            val colDonation1 = TextView(context)
            colDonation1.text = donor!!.foodCategory
            rowDonation.addView(colDonation1)

            val colDonation2 = TextView(context)
            colDonation2.text = donor!!.amount
            rowDonation.addView(colDonation2)
            tblDonationInfo.addView(rowDonation)

            val fieldName = arrayOf(
                "Date",
                "Time",
                "Location"
            )
            val fieldValue = arrayOf(
                donor!!.date,
                donor!!.time,
                donor!!.location
            )

            val tblRow = TableRow(context)
            val colFirstPickup = TextView(context)
            colFirstPickup.text = "Donor Name"
            tblRow.addView(colFirstPickup)

            val colFirstPickupValue = TextView(context)
            Helper.loadDonorName(donor!!.donorId, colFirstPickupValue)
            tblRow.addView(colFirstPickupValue)
            tblPickupInfo.addView(tblRow)

            for (i in fieldName.indices) {
                val tableRow = TableRow(context)

                val tvFieldName = TextView(context)
                tvFieldName.text = fieldName[i]
                tableRow.addView(tvFieldName)

                val tvFieldValue = TextView(context)
                tvFieldValue.text = fieldValue[i]
                tableRow.addView(tvFieldValue)

                tblPickupInfo.addView(tableRow)
            }
        }
    }
}