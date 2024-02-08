package ru.korobeynikov.newsapplication.domain.classes

data class NewsDomain(
    val id: Int = 0,
    val title: String?,
    val imageNews: String?,
    val descriptionNews: String?,
    val urlNews: String?,
    val imageSource: Int?,
    val nameSource: String?,
    val date: Long?,
    val textNews: String?,
    var dateSaving: Long? = 0,
)
