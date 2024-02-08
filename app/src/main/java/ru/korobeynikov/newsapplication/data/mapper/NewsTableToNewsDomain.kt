package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.NewsTable
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain

class NewsTableToNewsDomain {
    companion object {
        fun convertNewsTableToNewsDomain(newsTable: NewsTable) = NewsDomain(
            newsTable.idDomain,
            newsTable.title,
            newsTable.imageNews,
            newsTable.descriptionNews,
            newsTable.urlNews,
            newsTable.imageSource,
            newsTable.nameSource,
            newsTable.date,
            newsTable.textNews,
            newsTable.dateSaving
        )
    }
}