package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.ArticleBySourceTable
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain

class ArticleBySourceTableToNewsDomain {
    companion object {
        fun convertArticleBySourceTableToNewsDomain(article: ArticleBySourceTable) = NewsDomain(
            article.idDomain,
            article.title,
            article.imageNews,
            article.descriptionNews,
            article.urlNews,
            article.imageSource,
            article.nameSource,
            article.date,
            article.textNews,
            article.dateSaving
        )
    }
}