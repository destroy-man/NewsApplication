package ru.korobeynikov.newsapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SourceTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String,
    val dateSaving: Long?,
    val idDomain: String,
)