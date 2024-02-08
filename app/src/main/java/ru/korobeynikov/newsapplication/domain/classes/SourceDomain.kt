package ru.korobeynikov.newsapplication.domain.classes

data class SourceDomain(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String,
    var dateSaving: Long? = 0,
)
