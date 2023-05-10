package rsd.mad.mykasihv1.models

import androidx.room.Entity
import java.io.Serializable
import androidx.room.PrimaryKey

@Entity(tableName = "redemption")
class Redemption : Serializable {
    @PrimaryKey var redemptionId: String = ""
    var donorId: String = ""
    var voucher: String = ""
    var points: Int = 0
    var timestamp : Long = 0
    constructor()
    constructor(
        redemptionId: String,
        donorId: String,
        voucher: String,
        points: Int,
        timestamp: Long
    ) {
        this.redemptionId = redemptionId
        this.donorId = donorId
        this.voucher = voucher
        this.points = points
        this.timestamp = timestamp
    }

}