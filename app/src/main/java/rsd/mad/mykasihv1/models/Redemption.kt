package rsd.mad.mykasihv1.models

import java.io.Serializable

class Redemption : Serializable {
    var donorId: String = ""
    var voucher: String = ""
    var points: Int = 0
    var timestamp : Long = 0
    constructor()
    constructor(
        donorId: String,
        voucher: String,
        points: Int,
        timestamp: Long
    ) {
        this.donorId = donorId
        this.voucher = voucher
        this.points = points
        this.timestamp = timestamp
    }

}