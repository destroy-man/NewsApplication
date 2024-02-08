package ru.korobeynikov.newsapplication.data.repositories

import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.mapper.SourceDomainToSourceTable
import ru.korobeynikov.newsapplication.data.mapper.SourceTableToSourceDomain
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain
import ru.korobeynikov.newsapplication.domain.repositories.SourceTableRepositoryInterface

class SourceTableRepository(private val newsDB: NewsDB) : SourceTableRepositoryInterface {

    override fun getAllSources(): List<SourceDomain> {
        val listSourcesDomain = ArrayList<SourceDomain>()
        val listSourceTable = newsDB.sourcesDao().getAllSources()
        for (sourceTable in listSourceTable)
            listSourcesDomain.add(
                SourceTableToSourceDomain.convertSourceTableToSourceDomain(
                    sourceTable
                )
            )
        return listSourcesDomain
    }

    override fun addSource(sourceDomain: SourceDomain) {
        val sourceTable = SourceDomainToSourceTable.convertSourceDomainToSourceTable(sourceDomain)
        newsDB.sourcesDao().addSources(sourceTable)
    }

    override fun deleteSource(sourceDomain: SourceDomain) {
        val sourceTable = newsDB.sourcesDao().getAllSources().find {
            it.name == sourceDomain.name
        }
        if (sourceTable != null)
            newsDB.sourcesDao().deleteSources(sourceTable)
    }
}