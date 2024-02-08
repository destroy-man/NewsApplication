package ru.korobeynikov.newsapplication.presentation.filter_screen

data class FiltersState(
    val typeSort: String,
    val fromDate: Long?,
    val toDate: Long?,
    val language: String?,
)
