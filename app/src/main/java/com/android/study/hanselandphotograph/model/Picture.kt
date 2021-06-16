package com.android.study.hanselandphotograph.model

import java.io.Serializable
import java.time.LocalDate

data class Picture(val id: Int, var title:String, val path:String, val lat:Double, val long:Double):Serializable {
}