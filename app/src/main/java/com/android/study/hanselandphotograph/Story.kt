package com.android.study.hanselandphotograph

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Story(val id: Int, val name: String, val comment: String, val route: ArrayList<LatLng>, val picture: ArrayList<LatLng>): Serializable {
}