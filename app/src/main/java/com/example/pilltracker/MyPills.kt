package com.example.pilltracker
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class   MyPills(
    val id: Int,
    val userName: String,
    val nameOfMedicine: String,
    val dose: String,
    val timeToTakeMed: String,
    val additionalNotes: String,
    val remainingMedicine: String
) : Parcelable

