package com.doaha

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DocData(val Ack: String, val Welcome : String, val History: String, val RAP: String, val Elders: String) : Parcelable {
}