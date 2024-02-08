package ru.korobeynikov.newsapplication.presentation.mapper

import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain
import ru.korobeynikov.newsapplication.presentation.news.News
import java.util.Date

class NewsToArticleByCategoryDomain {
    companion object {
        fun convertNewsToArticleByCategoryDomain(news: News, category: String) =
            ArticleByCategoryDomain(
                news.id,
                news.title,
                news.imageNews,
                news.descriptionNews,
                news.urlNews,
                news.imageSource,
                news.nameSource,
                news.date,
                news.textNews,
                category,
                Date().time
            )
    }
}