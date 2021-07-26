package com.aarabik.pockemon

import android.location.Location

class Pockemon{
    var name:String?=null
    var descrip:String?=null
    var image:Int?=null
    var power:Double?=null
    var location:Location?=null
    var isCatch:Boolean?=false
    constructor(name:String, descrip:String, image:Int, power:Double, lat : Double, lot : Double){
        this.name = name
        this.descrip=descrip
        this.image=image
        this.power=power
        this.isCatch=isCatch
        this.location=Location(name)
        this.location!!.latitude=lat
        this.location!!.longitude=lot

    }
}