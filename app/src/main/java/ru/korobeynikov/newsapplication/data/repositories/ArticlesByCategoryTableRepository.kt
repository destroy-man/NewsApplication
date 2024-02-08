package ru.korobeynikov.newsapplication.data.repositories

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.korobeynikov.newsapplication.data.database.ArticleByCategoryTable
import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.mapper.ArticleByCategoryDomainToArticleByCategoryTable
import ru.korobeynikov.newsapplication.data.mapper.ArticleByCategoryTableToArticleByCategoryDomain
import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain
import ru.korobeynikov.newsapplication.domain.repositories.ArticleByCategoryTableRepositoryInterface

class ArticlesByCategoryTableRepository(private val newsDB: NewsDB) :
    ArticleByCategoryTableRepositoryInterface {

    override fun getAllArticlesByCategory(category: String): Single<List<ArticleByCategoryDomain>> {
        return newsDB.articlesByCategoryDao().getAllArticlesByCategory(category).map {
            it.map { article ->
                ArticleByCategoryTableToArticleByCategoryDomain.convertArticleByCategoryTableToArticleByCategoryDomain(
                    article
                )
            }
        }
    }

    override fun addArticleByCategory(articleByCategoryDomain: ArticleByCategoryDomain) {
        val articleByCategoryTable =
            ArticleByCategoryDomainToArticleByCategoryTable.convertArticleByCategoryDomainToArticleByCategoryTable(
                articleByCategoryDomain
            )
        newsDB.articlesByCategoryDao().addArticleByCategory(articleByCategoryTable)
    }

    override fun deleteArticleByCategory(
        articleByCategoryDomain: ArticleByCategoryDomain,
        category: String,
    ) {
        newsDB.articlesByCategoryDao().getAllArticlesByCategory(category)
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<List<ArticleByCategoryTable>> {

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {}

                override fun onSuccess(listArticles: List<ArticleByCategoryTable>) {
                    val articleByCategoryTable = listArticles.find {
                        it.title == articleByCategoryDomain.title && it.descriptionNews == articleByCategoryDomain.descriptionNews
                                && it.textNews == articleByCategoryDomain.textNews && it.nameSource == articleByCategoryDomain.nameSource
                    }
                    if (articleByCategoryTable != null)
                        newsDB.articlesByCategoryDao()
                            .deleteArticleByCategory(articleByCategoryTable)
                }
            })
    }
}