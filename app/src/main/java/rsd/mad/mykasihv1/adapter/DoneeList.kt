package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.RowDoneeBinding
import rsd.mad.mykasihv1.filter.FilterDonee
import rsd.mad.mykasihv1.models.RequestDonation
import rsd.mad.mykasihv1.ui.home.DonorHomeFragmentDirections

class DoneeList : RecyclerView.Adapter<DoneeList.HolderDonee>, Filterable {

    private val context: Context
    public var doneeArrayList: ArrayList<RequestDonation>
    private val filterList: ArrayList<RequestDonation>

    private lateinit var binding: RowDoneeBinding

    private var filter : FilterDonee? = null

    constructor(context: Context, doneeArrayList: ArrayList<RequestDonation>) : super() {
        this.context = context
        this.doneeArrayList = doneeArrayList
        this.filterList = doneeArrayList
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
        Picasso.with(context).load(orgImage).placeholder(R.drawable.progress_animation).error(R.drawable.try_later).into(holder.ivDoneeImage)
        Helper.loadDoneeCity(doneeId = doneeId, holder.tvCity)
        holder.itemView.setOnClickListener {
            var action = DonorHomeFragmentDirections.actionNavDonorHomeToNavDoneeDetails(model)
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterDonee(filterList, this)
        }
        return filter as FilterDonee
    }

    inner class HolderDonee(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDoneeName = binding.tvDonee
        var tvCity = binding.tvCity
        var ivDoneeImage = binding.ivDoneeImage
    }
}