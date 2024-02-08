package ru.korobeynikov.newsapplication.presentation.news

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain
import ru.korobeynikov.newsapplication.domain.repositories.NewsRepositoryInterface
import ru.korobeynikov.newsapplication.presentation.filter_screen.FiltersFragment
import ru.korobeynikov.newsapplication.presentation.mapper.NewsDomainToNews
import ru.korobeynikov.newsapplication.presentation.mapper.NewsToNewsDomain
import java.util.Date

class NewsViewModel(private val newsRepositoryInterface: NewsRepositoryInterface) : ViewModel() {

    companion object {
        var searchWord = ""
    }

    private var sortByFilter = ""
    private var fromDateFilter = 0L
    private var toDateFilter = 0L

    private val flowNews = flow {
        val allNews = ArrayList<NewsDomain>()
        val allNewsDomain = newsRepositoryInterface.getAllNews()
        for (newsDomain in allNewsDomain) {
            val days14 = Date().time - 14 * 1000 * 60 * 60 * 24
            newsDomain.dateSaving?.let {
                if (it <= days14)
                    newsRepositoryInterface.deleteNews(newsDomain)
                else
                    allNews.add(newsDomain)
            }
        }
        emit(allNews)
    }

    private val flowFilterNews = flow {
        val allNews = ArrayList<NewsDomain>()
        val allNewsDomain = newsRepositoryInterface.getAllNews()

        var filterList = allNewsDomain.filter {
            (it.title != null && it.title.contains(searchWord, true)) ||
                    (it.descriptionNews != null && it.descriptionNews.contains(
                        searchWord,
                        true
                    )) ||
                    (it.textNews != null && it.textNews.contains(searchWord, true))
        }

        if (fromDateFilter > 0 && toDateFilter > 0)
            filterList = filterList.filter {
                it.date != null && it.date >= fromDateFilter && it.date <= toDateFilter
            }

        if (sortByFilter.isNotEmpty())
            filterList = filterList.sortedBy {
                it.date
            }

        for (newsDomain in filterList)
            allNews.add(newsDomain)
        emit(allNews)
    }

    fun saveNewsToDB(news: News) {
        val newsDomain = NewsToNewsDomain.convertNewsToNewsDomain(news)
        newsRepositoryInterface.addNews(newsDomain)
    }

    suspend fun deleteNewsFromDB(news: News) {
        flowNews.collect { allNews ->
            val newsDomain = allNews.find {
                it.title == news.title && it.descriptionNews == news.descriptionNews
                        && it.textNews == news.textNews && it.nameSource == news.nameSource
            }
            if (newsDomain != null)
                newsRepositoryInterface.deleteNews(newsDomain)
        }
    }

    suspend fun getNews(news: News): Flow<News?> {
        return flow {
            flowNews.collect { allNews ->
                val findingNewsDomain = allNews.find {
                    it.title == news.title && it.descriptionNews == news.descriptionNews
                            && it.textNews == news.textNews && it.nameSource == news.nameSource
                }
                if (findingNewsDomain != null)
                    emit(NewsDomainToNews.convertNewsDomainToNews(findingNewsDomain))
                else emit(null)
            }
        }
    }

    fun getAllNews(): Flow<ArrayList<News>> {
        return flow {
            flowNews.collect { allNews ->
                val listNews = ArrayList<News>()
                for (newsDomain in allNews)
                    listNews.add(NewsDomainToNews.convertNewsDomainToNews(newsDomain))
                emit(listNews)
            }
        }
    }

    fun getFilterNews(search: String = searchWord): Flow<ArrayList<News>> {
        searchWord = search
        return flow {
            flowFilterNews.collect { filterNews ->
                val listNews = ArrayList<News>()
                for (newsDomain in filterNews)
                    listNews.add(NewsDomainToNews.convertNewsDomainToNews(newsDomain))
                emit(listNews)
            }
        }
    }

    fun fillFilters(): Int {
        var countFilters = 0
        val filtersState = FiltersFragment.filtersState
        if (filtersState != null) {
            sortByFilter = filtersState.typeSort
            if (filtersState.fromDate != null && filtersState.toDate != null) {
                fromDateFilter = filtersState.fromDate
                toDateFilter = filtersState.toDate
                countFilters++
            }
        }
        return countFilters
    }
}