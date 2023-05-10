package rsd.mad.mykasihv1.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rsd.mad.mykasihv1.models.Redemption

class RedemptionViewModel (application: Application): AndroidViewModel(application) {
    //LiveData gives us updated contacts when they change
    var redemptionList : LiveData<List<Redemption>>
    private val repository: RedemptionRepository

    init {
        val redemptionDao = RedemptionDatabase.getDatabase(application).redemptionDao()
        repository = RedemptionRepository(redemptionDao)
        redemptionList = repository.allRedemption
    }

    fun addRedemption(redemption: Redemption) = viewModelScope.launch{
        repository.add(redemption)
    }
}
