package com.example.reading_cycle.library.repository

import android.os.Parcel
import android.os.Parcelable

class LibraryRepository() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LibraryRepository> {
        override fun createFromParcel(parcel: Parcel): LibraryRepository {
            return LibraryRepository(parcel)
        }

        override fun newArray(size: Int): Array<LibraryRepository?> {
            return arrayOfNulls(size)
        }
    }
}