package com.android.study.hanselandphotograph

import java.io.Serializable
import java.time.LocalDate

data class Story(val id: Int, val date: LocalDate, val name: String, val comment: String, val route: ArrayList<Location>, val picture: ArrayList<Location>): Serializable {
}