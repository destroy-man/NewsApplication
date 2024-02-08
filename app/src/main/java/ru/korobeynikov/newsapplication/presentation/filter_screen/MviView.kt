package ru.korobeynikov.newsapplication.presentation.filter_screen

interface MviView {

    fun initRender(state: FiltersState)

    fun render(state: FiltersState)
}