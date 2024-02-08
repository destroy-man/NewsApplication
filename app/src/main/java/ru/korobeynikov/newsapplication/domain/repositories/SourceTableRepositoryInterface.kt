package ru.korobeynikov.newsapplication.domain.repositories

import ru.korobeynikov.newsapplication.domain.classes.SourceDomain

interface SourceTableRepositoryInterface {

    fun getAllSources(): List<SourceDomain>

    fun addSource(sourceDomain: SourceDomain)

    fun deleteSource(sourceDomain: SourceDomain)
}