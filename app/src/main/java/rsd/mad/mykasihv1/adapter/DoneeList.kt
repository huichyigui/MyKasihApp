package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.databinding.RowDoneeBinding
import rsd.mad.mykasihv1.models.RequestDonation
import rsd.mad.mykasihv1.ui.home.DonorHomeFragmentDirections

class DoneeList : RecyclerView.Adapter<DoneeList.HolderDonee> {

    private val context: Context
    private val doneeArrayList: ArrayList<RequestDonation>
    private lateinit var binding: RowDoneeBinding

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
        val orgImage = model.orgImage

        holder.tvDoneeName.text = doneeName
        Picasso.with(context).load(orgImage).into(holder.ivDoneeImage)
        Helper.loadDoneeCity(doneeId = doneeId, holder.tvCity)
        holder.itemView.setOnClickListener {
            var action = DonorHomeFragmentDirections.actionNavDonorHomeToNavDoneeDetails(model)
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }

    inner class HolderDonee(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDoneeName = binding.tvDonee
        var tvCity = binding.tvCity
        var ivDoneeImage = binding.ivDoneeImage
    }
}