package ru.korobeynikov.newsapplication.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.repositories.ArticlesByCategoryTableRepository
import ru.korobeynikov.newsapplication.data.repositories.ArticlesRepository
import ru.korobeynikov.newsapplication.presentation.headlines_screen.HeadlinesPresenter

@Module
class HeadlinesPresenterModule {

    @Provides
    fun provideHeadlinesPresenter(
        articlesRepository: ArticlesRepository,
        articlesByCategoryTableRepository: ArticlesByCategoryTableRepository,
    ) = HeadlinesPresenter(articlesRepository, articlesByCategoryTableRepository)

    @Provides
    fun provideHeadlinesRepository(retrofit: Retrofit) = ArticlesRepository(retrofit)

    @Provides
    fun provideArticlesByCategoryTableRepository(newsDB: NewsDB) =
        ArticlesByCategoryTableRepository(newsDB)

    @Provides
    @ApplicationScope
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl("https://newsapi.org/v2/").build()
}