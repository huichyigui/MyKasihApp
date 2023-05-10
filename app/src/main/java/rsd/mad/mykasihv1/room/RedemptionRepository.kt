package rsd.mad.mykasihv1.room

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rsd.mad.mykasihv1.models.Redemption

class RedemptionRepository(private val redemptionDao: RedemptionDao){
    //Room execute all queries on a separate thread
    val allRedemption: LiveData<List<Redemption>> = redemptionDao.getAllRedemption()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun add(redemption: Redemption){
        redemptionDao.insert(redemption)
    }
}