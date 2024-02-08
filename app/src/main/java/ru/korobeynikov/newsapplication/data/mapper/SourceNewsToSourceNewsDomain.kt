package ru.korobeynikov.newsapplication.data.mapper

import ru.korobeynikov.newsapplication.data.network.SourceNews
import ru.korobeynikov.newsapplication.domain.classes.SourceNewsDomain

class SourceNewsToSourceNewsDomain {
    companion object{
        fun convertSourceNewsToSourceNewsDomain(sourceNews: SourceNews) = SourceNewsDomain(
            sourceNews.id,
            sourceNews.name,
        )
    }
}