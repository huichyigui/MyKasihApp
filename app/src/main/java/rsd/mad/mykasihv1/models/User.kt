package rsd.mad.mykasihv1.models

class User {
    var name : String = ""
    var email : String = ""
    var mobile : String = ""
    var city : String = ""
    var address : String = ""
    var profileImage : String = ""
    var point : Int = 0
    var device: String = ""

    constructor(name: String, email: String, mobile: String, city: String, address: String) {
        this.name = name
        this.email = email
        this.mobile = mobile
        this.city = city
        this.address = address
        profileImage = ""
        point = 0
        device = ""
    }
}