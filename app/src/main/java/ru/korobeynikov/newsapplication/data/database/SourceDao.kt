package ru.korobeynikov.newsapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SourceDao {

    @Query("SELECT * FROM sourcetable")
    fun getAllSources(): List<SourceTable>

    @Insert
    fun addSources(newsTable: SourceTable)

    @Delete
    fun deleteSources(newsTable: SourceTable)
}