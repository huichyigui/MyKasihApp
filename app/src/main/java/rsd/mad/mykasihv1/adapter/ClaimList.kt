package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.databinding.RowClaimBinding
import rsd.mad.mykasihv1.models.Donation
import rsd.mad.mykasihv1.ui.history.MyClaimFragmentDirections

class ClaimList : RecyclerView.Adapter<ClaimList.HolderClaim> {

    private val context : Context
    private var claimArrayList: ArrayList<Donation>
    private lateinit var binding: RowClaimBinding

    constructor(context: Context, claimArrayList: ArrayList<Donation>) : super() {
        this.context = context
        this.claimArrayList = claimArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderClaim {
        binding = RowClaimBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderClaim(binding.root)
    }

    override fun getItemCount(): Int {
        return claimArrayList.size
    }

    override fun onBindViewHolder(holder: HolderClaim, position: Int) {
        val model = claimArrayList[position]
        val donorId = model.donorId
        val location = model.location
        var timestamp = model.timestamp

        Helper.loadDonorName(donorId = donorId, holder.tvDonorNameDonation)
        holder.tvLocationDonation.text = location
        holder.tvTimestampDonation.text = Helper.convertLongToTime(timestamp)
        holder.itemView.setOnClickListener {
            var action = MyClaimFragmentDirections.actionNavMyClaimToNavClaimDetail(model)
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }

    inner class HolderClaim(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDonorNameDonation = binding.tvDonorNameDonation
        var tvLocationDonation = binding.tvLocationDonation
        var tvTimestampDonation = binding.tvTimestampDonation
    }
}