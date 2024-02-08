package ru.korobeynikov.newsapplication.data.repositories

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import ru.korobeynikov.newsapplication.data.mapper.ArticleToArticleDomain
import ru.korobeynikov.newsapplication.data.mapper.SourceDataToSourceDomain
import ru.korobeynikov.newsapplication.data.network.ArticlesApi
import ru.korobeynikov.newsapplication.domain.classes.ArticleDomain
import ru.korobeynikov.newsapplication.domain.repositories.ArticlesRepositoryInterface
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain

class ArticlesRepository(private val retrofit: Retrofit) : ArticlesRepositoryInterface {

    companion object {
        const val API_ID = "b2ca6a0b242544fdbc1dc3f160c35704"
    }

    override fun getArticles(
        category: String,
        page: Int,
        search: String,
        language: String,
        sortBy: String,
        from: String,
        to: String,
    ): Single<List<ArticleDomain>> {
        val articlesApi = retrofit.create(ArticlesApi::class.java)
        val singleListArticles =
            articlesApi.articles("$category $search", page, API_ID, language, sortBy, from, to)
                .map {
                    it.articles.map { article ->
                        ArticleToArticleDomain.convertArticleToArticleDomain(article)
                    }
                }
        return singleListArticles
    }

    override fun getArticlesBySource(
        page: Int,
        source: String,
        language: String,
        sortBy: String,
        from: String,
        to: String,
    ): Single<List<ArticleDomain>> {
        val articlesApi = retrofit.create(ArticlesApi::class.java)
        val singleListArticles =
            articlesApi.articlesBySource(page, source, API_ID, language, sortBy, from, to).map {
                it.articles.map { article ->
                    ArticleToArticleDomain.convertArticleToArticleDomain(article)
                }
            }
        return singleListArticles
    }

    override suspend fun getSources(language: String): List<SourceDomain> {
        val articlesApi = retrofit.create(ArticlesApi::class.java)
        val listSourceDomain = ArrayList<SourceDomain>()
        val listSourceData = articlesApi.sources(API_ID, language).sources
        for (sourceData in listSourceData)
            listSourceDomain.add(SourceDataToSourceDomain.convertSourceDataToSourceDomain(sourceData))
        return listSourceDomain
    }
}