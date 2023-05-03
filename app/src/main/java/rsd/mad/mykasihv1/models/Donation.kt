package rsd.mad.mykasihv1.models

import java.io.Serializable

class Donation : Serializable {
    var donorId: String = ""
    var doneeId: String = ""
    var requestId: String = ""
    var foodCategory: String = ""
    var foodPackaging: String = ""
    var amount: String = ""
    var date: String = ""
    var time: String = ""
    var location: String = ""
    var status: String = ""
    var token: String = ""
    var proofImage: String = ""
    var timestamp : Long = 0
    constructor()
    constructor(
        donorId: String,
        doneeId: String,
        requestId: String,
        foodCategory: String,
        foodPackaging: String,
        amount: String,
        date: String,
        time: String,
        location: String,
        status: String,
        token: String,
        timestamp: Long
    ) {
        this.donorId = donorId
        this.doneeId = doneeId
        this.requestId = requestId
        this.foodCategory = foodCategory
        this.foodPackaging = foodPackaging
        this.amount = amount
        this.date = date
        this.time = time
        this.location = location
        this.status = status
        this.token = token
        this.proofImage = ""
        this.timestamp = timestamp
    }
}