package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.RowCommunityBinding
import rsd.mad.mykasihv1.models.Donation

class CommunityList : RecyclerView.Adapter<CommunityList.HolderCommunity> {

    private val context : Context
    private var communityArrayList: ArrayList<Donation>
    private lateinit var binding: RowCommunityBinding

    constructor(context: Context, communityArrayList: ArrayList<Donation>) : super() {
        this.context = context
        this.communityArrayList = communityArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCommunity {
        binding = RowCommunityBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCommunity(binding.root)
    }

    override fun getItemCount(): Int {
        return communityArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCommunity, position: Int) {
        val model = communityArrayList[position]
        val donorId = model.donorId
        val doneeId = model.doneeId
        val requestId = model.requestId
        val proofImage = model.proofImage

        Helper.loadDonorName(donorId = donorId, holder.tvDonorNameDonation)
        Helper.loadPax(doneeId = doneeId, requestId, holder.tvLocationDonation)
        Picasso.with(context).load(proofImage).placeholder(R.drawable.progress_animation).error(R.drawable.try_later).into(holder.ivProof)
    }

    inner class HolderCommunity(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDonorNameDonation = binding.tvDonorNameDonation
        var tvLocationDonation = binding.tvLocationDonation
        var ivProof = binding.ivProof
    }
}