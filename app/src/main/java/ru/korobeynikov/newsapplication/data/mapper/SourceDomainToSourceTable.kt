package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.SourceTable
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain

class SourceDomainToSourceTable {
    companion object {
        fun convertSourceDomainToSourceTable(sourceDomain: SourceDomain) = SourceTable(
            name = sourceDomain.name,
            description = sourceDomain.description,
            url = sourceDomain.url,
            category = sourceDomain.category,
            language = sourceDomain.language,
            country = sourceDomain.country,
            dateSaving = sourceDomain.dateSaving,
            idDomain = sourceDomain.id,
        )
    }
}