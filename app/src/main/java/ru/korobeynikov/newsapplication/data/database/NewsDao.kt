package ru.korobeynikov.newsapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewsDao {

    @Query("SELECT * FROM newstable")
    fun getAllNews(): List<NewsTable>

    @Insert
    fun addNews(newsTable: NewsTable)

    @Delete
    fun deleteNews(newsTable: NewsTable)
}