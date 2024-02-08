package ru.korobeynikov.newsapplication.presentation.error_screen

import android.os.Parcel
import android.os.Parcelable
import ru.korobeynikov.newsapplication.presentation.sources_screen.Source

data class ErrorState(
    val isInternetError: Boolean,
    val sourceError: String?,
    val sourceNews: Source? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isInternetError) 1 else 0)
        parcel.writeString(sourceError)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ErrorState> {
        override fun createFromParcel(parcel: Parcel): ErrorState {
            return ErrorState(parcel)
        }

        override fun newArray(size: Int): Array<ErrorState?> {
            return arrayOfNulls(size)
        }
    }
}
