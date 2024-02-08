package ru.korobeynikov.newsapplication.di

import dagger.Module
import dagger.Provides
import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.repositories.ArticlesBySourceTableRepository
import ru.korobeynikov.newsapplication.data.repositories.ArticlesRepository
import ru.korobeynikov.newsapplication.data.repositories.SourceTableRepository
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourcesViewModelFactory

@Module
class SourceModule {
    @Provides
    fun providesSourcesViewModelFactory(
        articlesRepository: ArticlesRepository,
        sourceTableRepository: SourceTableRepository,
        articlesBySourceTableRepository: ArticlesBySourceTableRepository,
    ) = SourcesViewModelFactory(
        articlesRepository,
        sourceTableRepository,
        articlesBySourceTableRepository
    )

    @Provides
    fun providesSourceTableRepository(newsDB: NewsDB) = SourceTableRepository(newsDB)

    @Provides
    fun providesArticleBySourceTableRepository(newsDB: NewsDB) =
        ArticlesBySourceTableRepository(newsDB)
}