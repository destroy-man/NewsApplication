package ru.korobeynikov.newsapplication.presentation.mapper

import ru.korobeynikov.newsapplication.domain.classes.NewsDomain
import ru.korobeynikov.newsapplication.presentation.news.News

class NewsDomainToNews {
    companion object {
        fun convertNewsDomainToNews(newsDomain: NewsDomain) = News(
            newsDomain.id,
            newsDomain.title,
            newsDomain.imageNews,
            newsDomain.descriptionNews,
            newsDomain.urlNews,
            newsDomain.imageSource,
            newsDomain.nameSource,
            newsDomain.date,
            newsDomain.textNews
        )
    }
}