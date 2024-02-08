package ru.korobeynikov.newsapplication.presentation.news

import android.os.Parcel
import android.os.Parcelable

data class News(
    val id: Int,
    val title: String?,
    val imageNews: String?,
    val descriptionNews: String?,
    val urlNews: String?,
    val imageSource: Int?,
    val nameSource: String?,
    val date: Long?,
    val textNews: String?,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(imageNews)
        parcel.writeString(descriptionNews)
        parcel.writeString(urlNews)
        parcel.writeValue(imageSource)
        parcel.writeString(nameSource)
        parcel.writeValue(date)
        parcel.writeString(textNews)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<News> {
        override fun createFromParcel(parcel: Parcel): News {
            return News(parcel)
        }

        override fun newArray(size: Int): Array<News?> {
            return arrayOfNulls(size)
        }
    }
}