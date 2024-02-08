package ru.korobeynikov.newsapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArticleBySourceDao {

    @Query("SELECT * FROM articlebysourcetable WHERE nameSource LIKE :source")
    fun getAllArticlesBySource(source: String): List<ArticleBySourceTable>

    @Insert
    fun addArticleBySource(articleBySourceTable: ArticleBySourceTable)

    @Delete
    fun deleteArticleBySource(articleBySourceTable: ArticleBySourceTable)
}