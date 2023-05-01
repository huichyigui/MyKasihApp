package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.databinding.RowDoneeBinding
import rsd.mad.mykasihv1.models.RequestDonation

class DoneeList : RecyclerView.Adapter<DoneeList.HolderDonee> {

    private val context : Context
    private val doneeArrayList: ArrayList<RequestDonation>
    private lateinit var binding : RowDoneeBinding

    constructor(context: Context, doneeArrayList: ArrayList<RequestDonation>) : super() {
        this.context = context
        this.doneeArrayList = doneeArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDonee {
        binding = RowDoneeBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderDonee(binding.root)
    }

    override fun getItemCount(): Int {
        return doneeArrayList.size
    }

    override fun onBindViewHolder(holder: HolderDonee, position: Int) {
        val model = doneeArrayList[position]
        val doneeId = model.doneeId
        val doneeName = model.doneeName
        val description = model.description
        val orgImage = model.orgImage
        val timestamp = model.timestamp

        holder.tvDoneeName.text = doneeName
        Picasso.with(context).load(orgImage).into(holder.ivDoneeImage)
        Helper.loadDoneeCity(doneeId = doneeId, holder.tvCity)
    }

    inner class HolderDonee(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDoneeName = binding.tvDonee
        var tvCity = binding.tvCity
        var ivDoneeImage = binding.ivDoneeImage
    }
}