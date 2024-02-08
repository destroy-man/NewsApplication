package ru.korobeynikov.newsapplication.presentation.headlines_screen

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.korobeynikov.newsapplication.presentation.news.News

interface HeadlinesView : MvpView {

    @AddToEndSingle
    fun showNews(listNews: List<News>, category: String)

    @AddToEndSingle
    fun showError(isInternetError: Boolean)

    @AddToEndSingle
    fun showProgressBar(isVisible: Boolean)
}