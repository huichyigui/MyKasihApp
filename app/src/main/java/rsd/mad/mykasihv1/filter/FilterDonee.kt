package rsd.mad.mykasihv1.filter

import android.widget.Filter
import rsd.mad.mykasihv1.adapter.DoneeList
import rsd.mad.mykasihv1.models.RequestDonation

class FilterDonee : Filter {

    var filterList: ArrayList<RequestDonation>
    var doneeList: DoneeList

    constructor(filterList: ArrayList<RequestDonation>, doneeList: DoneeList) {
        this.filterList = filterList
        this.doneeList = doneeList
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<RequestDonation>()

            for (i in filterList.indices) {
                if (filterList[i].doneeName.lowercase().contains(constraint)) {
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults?) {
        doneeList.doneeArrayList = results!!.values as ArrayList<RequestDonation>
        doneeList.notifyDataSetChanged()
    }
}