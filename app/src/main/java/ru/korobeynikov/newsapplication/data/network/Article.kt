package ru.korobeynikov.newsapplication.data.network

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Article(
    @SerializedName("source")
    val sourceNews: SourceNews,
    @SerializedName("author")
    val author: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val urlToImage: String,
    @SerializedName("publishedAt")
    val publishedAt: Date,
    @SerializedName("content")
    val content: String,
)
