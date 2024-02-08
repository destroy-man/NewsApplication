package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.ArticleByCategoryTable
import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain

class ArticleByCategoryDomainToArticleByCategoryTable {
    companion object {
        fun convertArticleByCategoryDomainToArticleByCategoryTable(article: ArticleByCategoryDomain) =
            ArticleByCategoryTable(
                title = article.title,
                imageNews = article.imageNews,
                descriptionNews = article.descriptionNews,
                urlNews = article.urlNews,
                imageSource = article.imageSource,
                nameSource = article.nameSource,
                date = article.date,
                textNews = article.textNews,
                dateSaving = article.dateSaving,
                category = article.category,
                idDomain = article.id,
            )
    }
}