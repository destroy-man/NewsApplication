package ru.korobeynikov.newsapplication.domain.repositories

import io.reactivex.rxjava3.core.Single
import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain

interface ArticleByCategoryTableRepositoryInterface {

    fun getAllArticlesByCategory(category: String): Single<List<ArticleByCategoryDomain>>

    fun addArticleByCategory(articleByCategoryDomain: ArticleByCategoryDomain)

    fun deleteArticleByCategory(articleByCategoryDomain: ArticleByCategoryDomain, category: String)
}