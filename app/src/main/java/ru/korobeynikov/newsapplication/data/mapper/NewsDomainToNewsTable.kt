package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.NewsTable
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain

class NewsDomainToNewsTable {
    companion object {
        fun convertNewsDomainToNewsTable(newsDomain: NewsDomain) = NewsTable(
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