package ru.korobeynikov.newsapplication.data.network

import com.google.gson.annotations.SerializedName

data class Articles(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("articles")
    val articles: ArrayList<Article>,
)
