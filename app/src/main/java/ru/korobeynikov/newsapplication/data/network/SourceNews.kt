package ru.korobeynikov.newsapplication.data.network

import com.google.gson.annotations.SerializedName

data class SourceNews(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
)
