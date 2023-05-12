package rsd.mad.mykasihv1.ui.home

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.Helper.Companion.toPx
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentDonorDonationDetailBinding
import rsd.mad.mykasihv1.models.Donation

class DonorDonationDetailFragment : Fragment() {

    private lateinit var binding: FragmentDonorDonationDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonorDonationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = NavHostFragment.findNavController(this).currentBackStackEntry?.arguments
        val donation = args?.getSerializable("donation") as? Donation

        with(binding) {
            tblDonationDetail.removeAllViews()
            val fieldName = arrayOf(
                "Category",
                "Packaging",
                "Contribution",
                "Preferred Date",
                "Preferred Time",
                "Delivery Token",
                "Pickup Location",
                "Token",
                "Status"
            )
            val fieldValue = arrayOf(
                donation!!.foodCategory,
                donation!!.foodPackaging,
                donation!!.amount,
                donation!!.date,
                donation!!.time,
                donation!!.token,
                donation!!.location,
                donation!!.token,
                donation!!.status
            )

            val row = TableRow(context)
            val col1 = TextView(context)
            col1.text = "Donee Name"
            col1.setTypeface(null, Typeface.BOLD)
            row.addView(col1)

            val col2 = TextView(context)
            Helper.loadDoneeName(donation!!.doneeId, col2)
            row.addView(col2)
            tblDonationDetail.addView(row)

            for (i in fieldName.indices) {
                val tableRow = TableRow(context)

                val tvFieldName = TextView(context)
                tvFieldName.text = fieldName[i]
                tvFieldName.setTypeface(null, Typeface.BOLD)
                tableRow.addView(tvFieldName)

                val tvFieldValue = TextView(context)
                tvFieldValue.text = fieldValue[i]
                tableRow.addView(tvFieldValue)

                tblDonationDetail.addView(tableRow)
            }

            if (donation!!.status != "Pending") {
                val rowProof = TableRow(context)
                val colProof1 = TextView(context)
                colProof1.text = "Proof Image"
                colProof1.setTypeface(null, Typeface.BOLD)
                rowProof.addView(colProof1)

                val rowProof2 = TableRow(context)
                val colProof2 = ImageView(context)
                val layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f)
                layoutParams.height =  200.toPx(requireContext())
                colProof2.layoutParams = layoutParams
                colProof2.scaleType = ImageView.ScaleType.CENTER_CROP
                Picasso.with(context).load(donation!!.proofImage).placeholder(R.drawable.progress_animation).error(R.drawable.try_later).into(colProof2)
                rowProof2.addView(colProof2)
                tblDonationDetail.addView(rowProof)
                tblDonationDetail.addView(rowProof2)
            }
        }
    }
}