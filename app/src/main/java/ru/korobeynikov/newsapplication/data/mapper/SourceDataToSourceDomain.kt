package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.network.SourceData
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain

class SourceDataToSourceDomain {
    companion object {
        fun convertSourceDataToSourceDomain(sourceData: SourceData) = SourceDomain(
            sourceData.id,
            sourceData.name,
            sourceData.description,
            sourceData.url,
            sourceData.category,
            sourceData.language,
            sourceData.country
        )
    }
}