package ru.korobeynikov.newsapplication.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.korobeynikov.newsapplication.data.database.NewsDB
import ru.korobeynikov.newsapplication.data.repositories.NewsRepository
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModelFactory

@Module
class NewsModule(private val context: Context) {

    @Provides
    fun providesNewsViewModelFactory(newsRepository: NewsRepository) =
        NewsViewModelFactory(newsRepository)

    @Provides
    fun providesNewsRepository(newsDB: NewsDB) = NewsRepository(newsDB)

    @Provides
    @ApplicationScope
    fun providesNewsDatabase() = Room.databaseBuilder(context, NewsDB::class.java, "News").build()
}