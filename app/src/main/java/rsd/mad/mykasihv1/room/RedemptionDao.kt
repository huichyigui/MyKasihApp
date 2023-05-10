package rsd.mad.mykasihv1.room

import androidx.lifecycle.LiveData
import androidx.room.*
import rsd.mad.mykasihv1.models.Redemption

@Dao
interface RedemptionDao {
    @Query("SELECT * FROM redemption ORDER BY timestamp ASC")
    fun getAllRedemption(): LiveData<List<Redemption>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(redemption: Redemption)

}