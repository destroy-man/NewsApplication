package ru.korobeynikov.newsapplication.domain.repositories

import ru.korobeynikov.newsapplication.domain.classes.NewsDomain

interface NewsRepositoryInterface {

    fun getAllNews(): List<NewsDomain>

    fun addNews(newsDomain: NewsDomain)

    fun deleteNews(newsDomain: NewsDomain)
}