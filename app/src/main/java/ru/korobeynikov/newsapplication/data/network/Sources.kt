package ru.korobeynikov.newsapplication.data.network

import com.google.gson.annotations.SerializedName

data class Sources(
    @SerializedName("status")
    val status: String,
    @SerializedName("sources")
    val sources: ArrayList<SourceData>,
)
