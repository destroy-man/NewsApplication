package ru.korobeynikov.newsapplication.domain.repositories

import ru.korobeynikov.newsapplication.domain.classes.NewsDomain

interface ArticleBySourceTableRepositoryInterface {

    fun getAllArticlesBySource(source: String): List<NewsDomain>

    fun addArticleBySource(newsDomain: NewsDomain)

    fun deleteArticleBySource(newsDomain: NewsDomain, source: String)
}