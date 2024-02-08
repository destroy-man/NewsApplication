package ru.korobeynikov.newsapplication.domain.repositories

import io.reactivex.rxjava3.core.Single
import ru.korobeynikov.newsapplication.domain.classes.ArticleDomain
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain

interface ArticlesRepositoryInterface {

    fun getArticles(
        category: String,
        page: Int,
        search: String,
        language: String,
        sortBy: String,
        from: String,
        to: String,
    ): Single<List<ArticleDomain>>

    fun getArticlesBySource(
        page: Int,
        source: String,
        language: String,
        sortBy: String,
        from: String,
        to: String,
    ): Single<List<ArticleDomain>>

    suspend fun getSources(language: String): List<SourceDomain>
}