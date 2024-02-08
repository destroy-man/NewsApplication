package ru.korobeynikov.newsapplication.presentation.mapper

import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain
import ru.korobeynikov.newsapplication.presentation.news.News

class ArticleByCategoryDomainToNews {
    companion object {
        fun convertArticleByCategoryDomainToNews(article: ArticleByCategoryDomain) = News(
            article.id,
            article.title,
            article.imageNews,
            article.descriptionNews,
            article.urlNews,
            article.imageSource,
            article.nameSource,
            article.date,
            article.textNews
        )
    }
}