package ru.korobeynikov.newsapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NewsTable::class, SourceTable::class, ArticleBySourceTable::class, ArticleByCategoryTable::class],
    version = 1
)
abstract class NewsDB : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    abstract fun sourcesDao(): SourceDao

    abstract fun articlesBySourceDao(): ArticleBySourceDao

    abstract fun articlesByCategoryDao(): ArticleByCategoryDao
}