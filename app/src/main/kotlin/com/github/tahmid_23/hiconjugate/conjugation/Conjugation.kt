package com.github.tahmid_23.hiconjugate.conjugation

import android.os.Parcel
import android.os.Parcelable

data class Conjugation(val verb: String,
                       val translation: String,
                       val group: String,
                       val type: String,
                       val particle: String?,
                       val subject: String?,
                       val aux: String?,
                       val conjugation: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(verb)
        parcel.writeString(translation)
        parcel.writeString(group)
        parcel.writeString(type)
        parcel.writeString(particle)
        parcel.writeString(subject)
        parcel.writeString(aux)
        parcel.writeString(conjugation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Conjugation> {
        override fun createFromParcel(parcel: Parcel): Conjugation {
            return Conjugation(parcel)
        }

        override fun newArray(size: Int): Array<Conjugation?> {
            return arrayOfNulls(size)
        }
    }

}