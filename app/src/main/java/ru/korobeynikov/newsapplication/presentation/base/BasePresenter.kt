package ru.korobeynikov.newsapplication.presentation.base

interface BasePresenter {
    fun fillFilters(isInternetAvailable: Boolean): Int
}