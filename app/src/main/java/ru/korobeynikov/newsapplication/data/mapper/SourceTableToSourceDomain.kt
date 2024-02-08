package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.database.SourceTable
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain

class SourceTableToSourceDomain {
    companion object {
        fun convertSourceTableToSourceDomain(sourceTable: SourceTable) = SourceDomain(
            sourceTable.idDomain,
            sourceTable.name,
            sourceTable.description,
            sourceTable.url,
            sourceTable.category,
            sourceTable.language,
            sourceTable.country,
            sourceTable.dateSaving
        )
    }
}