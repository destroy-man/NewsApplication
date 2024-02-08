package ru.korobeynikov.newsapplication.presentation.mapper

import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain
import ru.korobeynikov.newsapplication.presentation.sources_screen.Source

class SourceDomainToSource {
    companion object {
        fun convertSourceDomainToSource(sourceDomain: SourceDomain): Source {
            val image = when (sourceDomain.name) {
                "ABC News" -> R.drawable.abc_news_icon
                "CNBC" -> R.drawable.cnbc_icon
                "BBC News" -> R.drawable.bbc_news_icon
                "Reuters" -> R.drawable.reuters_icon
                "YouTube" -> R.drawable.youtube_icon
                else -> R.drawable.other_source_icon
            }
            val description = "${sourceDomain.category} | ${sourceDomain.country}"
            return Source(
                sourceDomain.id,
                image,
                sourceDomain.name,
                description,
                sourceDomain.language
            )
        }
    }
}