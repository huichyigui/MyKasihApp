package rsd.mad.mykasihv1.models

import java.io.Serializable

class RequestDonation : Serializable {
    var doneeId : String = ""
    var doneeName : String = ""
    var description : String = ""
    var pax : Int = 0
    var orgImage : String = ""
    var timestamp : Long = 0

    constructor()

    constructor(doneeId : String, doneeName : String, description : String, pax : Int, orgImage : String, timestamp : Long) {
        this.doneeId = doneeId
        this.doneeName = doneeName
        this.description = description
        this.pax = pax
        this.orgImage = orgImage
        this.timestamp = timestamp
    }
}