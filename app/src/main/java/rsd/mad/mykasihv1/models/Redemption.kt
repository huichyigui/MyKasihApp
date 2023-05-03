package rsd.mad.mykasihv1.models

import java.io.Serializable

class Redemption : Serializable {
    var donorId: String = ""
    var voucher: String = ""
    var points: String = ""
    var timestamp : Long = 0
    constructor()
    constructor(
        donorId: String,
        voucher: String,
        points: String,
        timestamp: Long
    ) {
        this.donorId = donorId
        this.voucher = voucher
        this.points = points
        this.timestamp = timestamp
    }

}