package com.android.study.hanselandphotograph.model

import java.io.Serializable
import java.time.LocalDate

data class Story(val id: Int, val date: String, val name: String, val comment: String): Serializable {
}