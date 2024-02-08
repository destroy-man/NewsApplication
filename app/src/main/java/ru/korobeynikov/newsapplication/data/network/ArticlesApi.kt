package ru.korobeynikov.newsapplication.data.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesApi {

    @GET("everything?pageSize=20")
    fun articles(
        @Query("q") searchWord: String,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): Single<Articles>

    @GET("everything?pageSize=20")
    fun articlesBySource(
        @Query("page") page: Int,
        @Query("sources") source: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): Single<Articles>

    @GET("sources")
    suspend fun sources(
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
    ): Sources
}