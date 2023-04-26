package com.example.pilltracker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Logs(
val id: Int,
val userName: String,
val medicineName: String,
val status: String,
val dateTaken: String,
val additionalNotes: String
) : Parcelable {
    var isSelected: Boolean = false
    var isRefreshing:Boolean = false
}