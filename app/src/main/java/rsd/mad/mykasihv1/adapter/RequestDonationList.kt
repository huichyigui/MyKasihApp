package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.databinding.RowRequestsBinding
import rsd.mad.mykasihv1.models.RequestDonation
import kotlin.collections.ArrayList

class RequestDonationList : RecyclerView.Adapter<RequestDonationList.HolderRequestDonation> {

    private val context : Context
    private val requestArrayList: ArrayList<RequestDonation>
    private lateinit var binding: RowRequestsBinding

    constructor(context: Context, requestArrayList: ArrayList<RequestDonation>) {
        this.context = context
        this.requestArrayList = requestArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRequestDonation {
        binding = RowRequestsBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderRequestDonation(binding.root)
    }

    override fun getItemCount(): Int {
        return requestArrayList.size
    }

    override fun onBindViewHolder(holder: HolderRequestDonation, position: Int) {
        val model = requestArrayList[position]
        val doneeName = model.doneeName
        val description = model.description
        val orgImage = model.orgImage
        val timestamp = model.timestamp

        holder.tvDoneeName.text = doneeName
        holder.tvDescription.text = description
        Picasso.with(context).load(orgImage).into(holder.ivOrgImage)
        holder.tvCreatedDate.text = Helper.convertLongToTime(timestamp)
    }

    inner class HolderRequestDonation(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvDoneeName = binding.tvDoneeName
        var tvDescription = binding.tvDescription
        var ivOrgImage = binding.ivOrgImage
        var tvCreatedDate = binding.tvCreatedDate
    }
}