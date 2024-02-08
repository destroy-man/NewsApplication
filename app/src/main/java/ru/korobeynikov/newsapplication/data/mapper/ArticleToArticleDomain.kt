package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.network.Article
import ru.korobeynikov.newsapplication.domain.classes.ArticleDomain

class ArticleToArticleDomain {
    companion object {
        fun convertArticleToArticleDomain(article: Article): ArticleDomain {
            val sourceNewsDomain =
                SourceNewsToSourceNewsDomain.convertSourceNewsToSourceNewsDomain(article.sourceNews)
            return ArticleDomain(
                sourceNewsDomain,
                article.author,
                article.title,
                article.description,
                article.url,
                article.urlToImage,
                article.publishedAt,
                article.content
            )
        }
    }
}