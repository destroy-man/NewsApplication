package ru.korobeynikov.newsapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArticleByCategoryTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val imageNews: String?,
    val descriptionNews: String?,
    val urlNews: String?,
    val imageSource: Int?,
    val nameSource: String?,
    val date: Long?,
    val textNews: String?,
    val category: String?,
    val dateSaving: Long?,
    val idDomain: Int,
)
