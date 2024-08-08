package com.example.reading_cycle.library.vm

import android.os.Parcel
import android.os.Parcelable

class LibraryViewModel() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LibraryViewModel> {
        override fun createFromParcel(parcel: Parcel): LibraryViewModel {
            return LibraryViewModel(parcel)
        }

        override fun newArray(size: Int): Array<LibraryViewModel?> {
            return arrayOfNulls(size)
        }
    }
}