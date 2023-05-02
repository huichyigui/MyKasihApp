package rsd.mad.mykasihv1.models

class Donation {
    var donorId: String = ""
    var doneeId: String = ""
    var foodCategory: String = ""
    var foodPackaging: String = ""
    var amount: String = ""
    var date: String = ""
    var time: String = ""
    var location: String = ""
    var status: String = ""
    var token: String = ""
    var timestamp : Long = 0

    constructor(
        donorId: String,
        doneeId: String,
        foodCategory: String,
        foodPackaging: String,
        amount: String,
        date: String,
        time: String,
        location: String,
        status: String,
        token: String
    ) {
        this.donorId = donorId
        this.doneeId = doneeId
        this.foodCategory = foodCategory
        this.foodPackaging = foodPackaging
        this.amount = amount
        this.date = date
        this.time = time
        this.location = location
        this.status = status
        this.token = token
    }
    constructor()
    constructor(
        donorId: String,
        doneeId: String,
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
        this.foodCategory = foodCategory
        this.foodPackaging = foodPackaging
        this.amount = amount
        this.date = date
        this.time = time
        this.location = location
        this.status = status
        this.token = token
        this.timestamp = timestamp
    }
}