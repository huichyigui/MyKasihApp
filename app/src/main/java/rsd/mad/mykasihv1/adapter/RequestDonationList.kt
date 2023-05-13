package rsd.mad.mykasihv1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import rsd.mad.mykasihv1.Helper
import rsd.mad.mykasihv1.R
import rsd.mad.mykasihv1.databinding.RowRequestsBinding
import rsd.mad.mykasihv1.models.RequestDonation
import java.util.HashMap
import kotlin.collections.ArrayList

class RequestDonationList : RecyclerView.Adapter<RequestDonationList.HolderRequestDonation> {

    private val context : Context
    private val requestArrayList: ArrayList<RequestDonation>
    private lateinit var binding: RowRequestsBinding
    private lateinit var auth: FirebaseAuth

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
        val status = model.status
        val timestamp = model.timestamp
        auth = Firebase.auth

        holder.tvDoneeName.text = doneeName
        holder.tvDescription.text = description
        Picasso.with(context).load(orgImage).placeholder(R.drawable.progress_animation).error(R.drawable.try_later).into(holder.ivOrgImage)
        holder.tvCreatedDate.text = Helper.convertLongToTime(timestamp)
        holder.tvStatus.text = status

        if (status == "Active"){
            holder.btnStatus.text = "Deactivate"
        }else {
            holder.btnStatus.text = "Activate"
        }

        holder.btnStatus.setOnClickListener {
            val hashMap: HashMap<String, Any> = HashMap()

            if (holder.btnStatus.text.toString() == "Activate") {
                hashMap["status"] = "Active"
                holder.btnStatus.text = "Deactivate"
            }else {
                hashMap["status"] = "Inactive"
                holder.btnStatus.text = "Activate"
            }

            Firebase.database.getReference("RequestDonation").child(auth.uid!!).child(timestamp.toString())
                .updateChildren(hashMap)
                .addOnSuccessListener {
                    toast("Status updated successfully")
                }
                .addOnFailureListener {
                    toast("Status failed to update")
                }
        }
    }

    inner class HolderRequestDonation(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvDoneeName = binding.tvDoneeName
        var tvDescription = binding.tvDescription
        var ivOrgImage = binding.ivOrgImage
        var tvCreatedDate = binding.tvCreatedDate
        var tvStatus = binding.tvStatus
        var btnStatus = binding.btnStatus
    }

    private fun toast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }
}