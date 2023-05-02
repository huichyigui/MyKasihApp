package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.databinding.RowDonorBinding
import rsd.mad.mykasihv1.models.Donation
import rsd.mad.mykasihv1.ui.home.DoneeHomeFragmentDirections

class DonorList : RecyclerView.Adapter<DonorList.HolderDonor> {

    private val context : Context
    private var donorArrayList: ArrayList<Donation>
    private lateinit var binding: RowDonorBinding

    constructor(context: Context, donorArrayList: ArrayList<Donation>) : super() {
        this.context = context
        this.donorArrayList = donorArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDonor {
        binding = RowDonorBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderDonor(binding.root)
    }

    override fun getItemCount(): Int {
        return donorArrayList.size
    }

    override fun onBindViewHolder(holder: HolderDonor, position: Int) {
        val model = donorArrayList[position]
        val donorId = model.donorId
        val location = model.location

        Helper.loadDonorImage(context, donorId, holder.ivDonorDonation)
        Helper.loadDonorName(donorId = donorId, holder.tvDonorNameDonation)
        holder.tvLocationDonation.text = location
        holder.itemView.setOnClickListener {
            var action = DoneeHomeFragmentDirections.actionNavDoneeHomeToNavDonationDetail(model)
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }

    inner class HolderDonor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDonorDonation = binding.ivDonorDonation
        val tvDonorNameDonation = binding.tvDonorNameDonation
        val tvLocationDonation = binding.tvLocationDonation
    }
}