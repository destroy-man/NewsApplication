package ru.korobeynikov.newsapplication.domain.classes

import java.util.Date

data class ArticleDomain(
    val sourceNews: SourceNewsDomain?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: Date?,
    val content: String?,
)
