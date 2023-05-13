package rsd.mad.mykasihv1.ui.community

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.FragmentLeaderboardBinding
import rsd.mad.mykasihv1.models.User
import java.text.NumberFormat
import java.util.*


class LeaderboardFragment : Fragment() {

    private lateinit var binding: FragmentLeaderboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getImage()
        populateLeaderboard()
    }

    private fun getImage() {
        val donorsList = mutableListOf<User>()
        val ref = Firebase.database.getReference("Users").child("Donor")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (donorSnapshot in snapshot.children) {
                    val donor = donorSnapshot.getValue(User::class.java)
                    donorsList.add(donor!!)
                }
                donorsList.sortByDescending { it.point }

                var top1Image = donorsList[0].profileImage
                var top2Image = donorsList[1].profileImage
                var top3Image = donorsList[2].profileImage

                if (top1Image.isNotEmpty())
                    Picasso.with(context).load(donorsList[0].profileImage)
                        .placeholder(R.drawable.progress_animation).error(R.drawable.try_later)
                        .into(binding.ivTop1)
                else
                    binding.ivTop1.setImageResource(R.drawable.empty)

                if (top2Image.isNotEmpty())
                    Picasso.with(context).load(donorsList[1].profileImage)
                        .placeholder(R.drawable.progress_animation).error(R.drawable.try_later)
                        .into(binding.ivTop2)
                else
                    binding.ivTop2.setImageResource(R.drawable.empty)

                if (top3Image.isNotEmpty())
                    Picasso.with(context).load(donorsList[2].profileImage)
                        .placeholder(R.drawable.progress_animation).error(R.drawable.try_later)
                        .into(binding.ivTop3)
                else
                    binding.ivTop3.setImageResource(R.drawable.empty)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun populateLeaderboard() {
        val donorsList = mutableListOf<User>()
        val ref = Firebase.database.getReference("Users").child("Donor")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (donorSnapshot in snapshot.children) {
                    val donor = donorSnapshot.getValue(User::class.java)
                    donorsList.add(donor!!)
                }

                donorsList.sortByDescending { it.point }

                val tableLayout = binding.tblLeaderboard
                val headerRow = TableRow(context)
                val headerRank = TextView(context)
                val headerName = TextView(context)
                val headerPoints = TextView(context)

                headerRank.text = getString(R.string.rank)
                headerName.text = getString(R.string.name)
                headerPoints.text = getString(R.string.point)
                headerRank.setTypeface(null, Typeface.BOLD)
                headerName.setTypeface(null, Typeface.BOLD)
                headerPoints.setTypeface(null, Typeface.BOLD)
                headerRow.addView(headerRank)
                headerRow.addView(headerName)
                headerRow.addView(headerPoints)
                tableLayout.addView(headerRow)

                for ((index, donor) in donorsList.withIndex()) {
                    if (index >= 10) break
                    val tableRow = TableRow(context)
                    val tvRank = TextView(context)
                    val tvName = TextView(context)
                    val tvPoints = TextView(context)

                    tvRank.text = (index + 1).toString()
                    tvName.text = donor.name
                    tvPoints.text = NumberFormat.getNumberInstance(Locale.US).format(donor.point)
                    tableRow.addView(tvRank)
                    tableRow.addView(tvName)
                    tableRow.addView(tvPoints)
                    tableLayout.addView(tableRow)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}