package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.databinding.RowDonationBinding
import rsd.mad.mykasihv1.models.Donation
import rsd.mad.mykasihv1.ui.history.MyDonationFragmentDirections

class DonationList : RecyclerView.Adapter<DonationList.HolderDonation> {

    private val context : Context
    private var donationArrayList: ArrayList<Donation>
    private lateinit var binding: RowDonationBinding

    constructor(context: Context, donationArrayList: ArrayList<Donation>) : super() {
        this.context = context
        this.donationArrayList = donationArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDonation {
        binding = RowDonationBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderDonation(binding.root)
    }

    override fun getItemCount(): Int {
        return donationArrayList.size
    }

    override fun onBindViewHolder(holder: HolderDonation, position: Int) {
        val model = donationArrayList[position]
        val doneeId = model.doneeId
        val location = model.location
        val timestamp = model.timestamp

        Helper.loadDoneeName(doneeId = doneeId, holder.tvDoneeNameDonation)
        holder.tvLocationDonation.text = location
        holder.tvTimestampDonation.text = Helper.convertLongToTime(timestamp)
        holder.itemView.setOnClickListener {
            var action = MyDonationFragmentDirections.actionNavMyDonationToNavDonorDonationDetail(model)
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }

    inner class HolderDonation(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDoneeNameDonation = binding.tvDoneeNameDonation
        val tvLocationDonation = binding.tvLocationDonation
        val tvTimestampDonation = binding.tvTimestampDonation
    }
}