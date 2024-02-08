package ru.korobeynikov.newsapplication.data.repositories

import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.mapper.ArticleBySourceTableToNewsDomain
import ru.korobeynikov.newsapplication.data.mapper.NewsDomainToArticleBySourceTable
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain
import ru.korobeynikov.newsapplication.domain.repositories.ArticleBySourceTableRepositoryInterface

class ArticlesBySourceTableRepository(private val newsDB: NewsDB) :
    ArticleBySourceTableRepositoryInterface {

    override fun getAllArticlesBySource(source: String): List<NewsDomain> {
        val listArticlesDomain = ArrayList<NewsDomain>()
        val listArticlesBySource = newsDB.articlesBySourceDao().getAllArticlesBySource(source)
        for (article in listArticlesBySource)
            listArticlesDomain.add(
                ArticleBySourceTableToNewsDomain.convertArticleBySourceTableToNewsDomain(
                    article
                )
            )
        return listArticlesDomain
    }

    override fun addArticleBySource(newsDomain: NewsDomain) {
        val articleBySourceTable =
            NewsDomainToArticleBySourceTable.convertNewsDomainToArticleBySourceTable(newsDomain)
        newsDB.articlesBySourceDao().addArticleBySource(articleBySourceTable)
    }

    override fun deleteArticleBySource(newsDomain: NewsDomain, source: String) {
        val articleBySourceTable =
            newsDB.articlesBySourceDao().getAllArticlesBySource(source).find {
                it.title == newsDomain.title && it.descriptionNews == newsDomain.descriptionNews
                        && it.textNews == newsDomain.textNews && it.nameSource == newsDomain.nameSource
            }
        if (articleBySourceTable != null)
            newsDB.articlesBySourceDao().deleteArticleBySource(articleBySourceTable)
    }
}