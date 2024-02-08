package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.ArticleByCategoryTable
import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain

class ArticleByCategoryTableToArticleByCategoryDomain {
    companion object {
        fun convertArticleByCategoryTableToArticleByCategoryDomain(article: ArticleByCategoryTable) =
            ArticleByCategoryDomain(
                article.idDomain,
                article.title,
                article.imageNews,
                article.descriptionNews,
                article.urlNews,
                article.imageSource,
                article.nameSource,
                article.date,
                article.textNews,
                article.category,
                article.dateSaving
            )
    }
}