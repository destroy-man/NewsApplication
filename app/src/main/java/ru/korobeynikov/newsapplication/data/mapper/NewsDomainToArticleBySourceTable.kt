package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.ArticleBySourceTable
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain

class NewsDomainToArticleBySourceTable {
    companion object {
        fun convertNewsDomainToArticleBySourceTable(newsDomain: NewsDomain) = ArticleBySourceTable(
            title = newsDomain.title,
            imageNews = newsDomain.imageNews,
            descriptionNews = newsDomain.descriptionNews,
            urlNews = newsDomain.urlNews,
            imageSource = newsDomain.imageSource,
            nameSource = newsDomain.nameSource,
            date = newsDomain.date,
            textNews = newsDomain.textNews,
            dateSaving = newsDomain.dateSaving,
            idDomain = newsDomain.id
        )
    }
}