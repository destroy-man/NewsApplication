package ru.korobeynikov.newsapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface ArticleByCategoryDao {
    @Query("SELECT * FROM articlebycategorytable WHERE category LIKE :category")
    fun getAllArticlesByCategory(category: String): Single<List<ArticleByCategoryTable>>

    @Insert
    fun addArticleByCategory(articleByCategoryTable: ArticleByCategoryTable)

    @Delete
    fun deleteArticleByCategory(articleByCategoryTable: ArticleByCategoryTable)
}