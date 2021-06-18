package com.android.study.hanselandphotograph.model

import java.io.Serializable
import java.time.LocalDate

data class Picture(var id: Int, var title:String, val path:String, var lat:Double, var long:Double):Serializable {
}