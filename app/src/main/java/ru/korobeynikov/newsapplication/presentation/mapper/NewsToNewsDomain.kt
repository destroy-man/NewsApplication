package ru.korobeynikov.newsapplication.presentation.mapper

import ru.korobeynikov.newsapplication.domain.classes.NewsDomain
import ru.korobeynikov.newsapplication.presentation.news.News
import java.util.Date

class NewsToNewsDomain {
    companion object {
        fun convertNewsToNewsDomain(news: News) = NewsDomain(
            news.id,
            news.title,
            news.imageNews,
            news.descriptionNews,
            news.urlNews,
            news.imageSource,
            news.nameSource,
            news.date,
            news.textNews,
            Date().time
        )
    }
}