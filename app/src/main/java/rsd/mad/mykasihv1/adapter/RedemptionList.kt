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
import rsd.mad.mykasihv1.databinding.RowRedemptionBinding
import rsd.mad.mykasihv1.models.Donation
import rsd.mad.mykasihv1.models.Redemption
import rsd.mad.mykasihv1.ui.history.MyDonationFragmentDirections

class RedemptionList : RecyclerView.Adapter<RedemptionList.HolderRedemption> {

    private val context : Context
    private var redemptionArrayList: ArrayList<Redemption>
    private lateinit var binding: RowRedemptionBinding

    constructor(context: Context, redemptionArrayList: ArrayList<Redemption>) : super() {
        this.context = context
        this.redemptionArrayList = redemptionArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRedemption {
        binding = RowRedemptionBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderRedemption(binding.root)
    }

    override fun getItemCount(): Int {
        return redemptionArrayList.size
    }

    override fun onBindViewHolder(holder: HolderRedemption, position: Int) {
        val model = redemptionArrayList[position]
        val voucher = model.voucher
        val points = model.points
        val timestamp = model.timestamp

        holder.tvVoucher.text = voucher
        holder.tvPoints.text = points
        holder.tvTimestampRedemption.text = Helper.convertLongToTime(timestamp)
    }

    inner class HolderRedemption(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVoucher = binding.tvVoucher
        val tvPoints = binding.tvPoints
        val tvTimestampRedemption = binding.tvTimestampRedemption
    }
}