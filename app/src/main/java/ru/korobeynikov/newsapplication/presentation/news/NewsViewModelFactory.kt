package ru.korobeynikov.newsapplication.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.korobeynikov.newsapplication.domain.repositories.NewsRepositoryInterface
import javax.inject.Inject

class NewsViewModelFactory @Inject constructor(private val newsRepositoryInterface: NewsRepositoryInterface) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepositoryInterface) as T
    }
}