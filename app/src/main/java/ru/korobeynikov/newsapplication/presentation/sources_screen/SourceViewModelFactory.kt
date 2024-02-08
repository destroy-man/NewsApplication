package ru.korobeynikov.newsapplication.presentation.sources_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.korobeynikov.newsapplication.domain.repositories.ArticleBySourceTableRepositoryInterface
import ru.korobeynikov.newsapplication.domain.repositories.ArticlesRepositoryInterface
import ru.korobeynikov.newsapplication.domain.repositories.SourceTableRepositoryInterface

class SourcesViewModelFactory(
    private val articleRepositoryInterface: ArticlesRepositoryInterface,
    private val sourceTableRepositoryInterface: SourceTableRepositoryInterface,
    private val articleBySourceTableRepositoryInterface: ArticleBySourceTableRepositoryInterface,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SourceViewModel(
            articleRepositoryInterface,
            sourceTableRepositoryInterface,
            articleBySourceTableRepositoryInterface
        ) as T
    }
}