package ru.korobeynikov.newsapplication.data.repositories

import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.mapper.NewsDomainToNewsTable
import ru.korobeynikov.newsapplication.data.mapper.NewsTableToNewsDomain
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain
import ru.korobeynikov.newsapplication.domain.repositories.NewsRepositoryInterface

class NewsRepository(private val newsDB: NewsDB) : NewsRepositoryInterface {

    override fun getAllNews(): List<NewsDomain> {
        val listNewsDomain = ArrayList<NewsDomain>()
        val listNewsTable = newsDB.newsDao().getAllNews()
        for (newsTable in listNewsTable)
            listNewsDomain.add(NewsTableToNewsDomain.convertNewsTableToNewsDomain(newsTable))
        return listNewsDomain
    }

    override fun addNews(newsDomain: NewsDomain) {
        val newsTable = NewsDomainToNewsTable.convertNewsDomainToNewsTable(newsDomain)
        newsDB.newsDao().addNews(newsTable)
    }

    override fun deleteNews(newsDomain: NewsDomain) {
        val newsTable = newsDB.newsDao().getAllNews().find {
            it.title == newsDomain.title && it.descriptionNews == newsDomain.descriptionNews
                    && it.textNews == newsDomain.textNews && it.nameSource == newsDomain.nameSource
        }
        if (newsTable != null)
            newsDB.newsDao().deleteNews(newsTable)
    }
}