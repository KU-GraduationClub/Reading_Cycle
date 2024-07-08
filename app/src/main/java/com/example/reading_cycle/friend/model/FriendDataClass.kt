package com.example.reading_cycle.friend.model

import android.os.Parcel
import android.os.Parcelable

data class Friend(
    var userIdx: String? = "",
    var userNickname: String? = "",
    var userPhoneNumber: String? = "",
    var userProfileImage: String? = "",
    var memo: String? = "",
    var isBookmarked: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userIdx)
        parcel.writeString(userNickname)
        parcel.writeString(userPhoneNumber)
        parcel.writeString(userProfileImage)
        parcel.writeString(memo)
        parcel.writeByte(if (isBookmarked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Friend> {
        override fun createFromParcel(parcel: Parcel): Friend {
            return Friend(parcel)
        }

        override fun newArray(size: Int): Array<Friend?> {
            return arrayOfNulls(size)
        }
    }
}
