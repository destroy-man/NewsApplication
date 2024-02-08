package ru.korobeynikov.newsapplication.presentation.sources_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.domain.classes.ArticleDomain
import ru.korobeynikov.newsapplication.domain.classes.NewsDomain
import ru.korobeynikov.newsapplication.domain.classes.SourceDomain
import ru.korobeynikov.newsapplication.domain.repositories.ArticleBySourceTableRepositoryInterface
import ru.korobeynikov.newsapplication.domain.repositories.ArticlesRepositoryInterface
import ru.korobeynikov.newsapplication.domain.repositories.SourceTableRepositoryInterface
import ru.korobeynikov.newsapplication.presentation.filter_screen.FiltersFragment
import ru.korobeynikov.newsapplication.presentation.mapper.NewsDomainToNews
import ru.korobeynikov.newsapplication.presentation.mapper.NewsToNewsDomain
import ru.korobeynikov.newsapplication.presentation.mapper.SourceDomainToSource
import ru.korobeynikov.newsapplication.presentation.news.News
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SourceViewModel(
    private val articlesRepository: ArticlesRepositoryInterface,
    private val sourceTableRepositoryInterface: SourceTableRepositoryInterface,
    private val articlesBySourceTableRepositoryInterface: ArticleBySourceTableRepositoryInterface,
) : ViewModel() {

    companion object {
        var listSources = ArrayList<Source>()
        var listNews = ArrayList<News>()

        var searchWordSource = ""
        var searchWordNews = ""
    }

    private var mutableSourcesLiveData = MutableLiveData<List<Source>>()
    var sourcesLiveData: LiveData<List<Source>> = mutableSourcesLiveData

    private var mutableNewsLiveData = MutableLiveData<List<News>>()
    var newsLiveData: LiveData<List<News>> = mutableNewsLiveData

    private var mutableErrorLiveData = MutableLiveData<Boolean>()
    var errorLiveData: LiveData<Boolean> = mutableErrorLiveData

    var articlesDisposable: Disposable? = null

    private var languageFilter = ""
    private var sortByFilter = ""
    private var fromDateFilter = 0L
    private var toDateFilter = 0L

    private val handler = CoroutineExceptionHandler { _, throwable ->
        mutableErrorLiveData.value = throwable is IOException
    }

    fun getSourcesFromNetwork(language: String = languageFilter) {
        viewModelScope.launch(handler) {
            withContext(Dispatchers.IO) {
                if (listSources.isEmpty()) {
                    var sourcesFromCash = sourceTableRepositoryInterface.getAllSources()
                    sourcesFromCash = clearCashSources(sourcesFromCash)
                    val listSourceDomain = articlesRepository.getSources(language)
                    for (sourceDomain in listSourceDomain) {
                        listSources.add(
                            SourceDomainToSource.convertSourceDomainToSource(
                                sourceDomain
                            )
                        )
                        if (sourcesFromCash.find { it.id == sourceDomain.id } == null) {
                            sourceDomain.dateSaving = Date().time
                            sourceTableRepositoryInterface.addSource(sourceDomain)
                        }
                    }
                }
            }
            mutableSourcesLiveData.value = listSources
        }
    }

    fun getNewsBySource(
        source: String?,
        nameSource: String?,
        language: String = languageFilter,
        sortBy: String = sortByFilter,
        from: Long = fromDateFilter,
        to: Long = toDateFilter,
        search: String = searchWordNews,
    ) {
        var fromDate = ""
        var toDate = ""
        if (from > 0 && to > 0) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            fromDate = formatter.format(Date(from))
            toDate = formatter.format(Date(to))
        }
        source?.let { it ->
            articlesRepository.getArticlesBySource(1, it, language, sortBy, fromDate, toDate)
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<List<ArticleDomain>> {

                    override fun onSubscribe(d: Disposable) {
                        articlesDisposable = d
                    }

                    override fun onError(e: Throwable) {
                        mutableErrorLiveData.postValue(e is IOException)
                    }

                    override fun onSuccess(articles: List<ArticleDomain>) {
                        var sourceNewsFromCash = emptyList<NewsDomain>()
                        nameSource?.let {
                            sourceNewsFromCash =
                                articlesBySourceTableRepositoryInterface.getAllArticlesBySource(it)
                            sourceNewsFromCash = clearCashSourceNews(sourceNewsFromCash, it)
                        }

                        listNews.clear()
                        val idImageSource = when (nameSource) {
                            "ABC News" -> R.drawable.abc_news_icon
                            "CNBC" -> R.drawable.cnbc_icon
                            "BBC News" -> R.drawable.bbc_news_icon
                            "Reuters" -> R.drawable.reuters_icon
                            "YouTube" -> R.drawable.youtube_icon
                            else -> R.drawable.other_source_icon
                        }
                        for ((id, article) in articles.withIndex()) {
                            val news = News(
                                id,
                                article.title,
                                article.urlToImage,
                                article.description,
                                article.url,
                                idImageSource,
                                article.sourceNews?.name,
                                article.publishedAt?.time,
                                article.content
                            )
                            val findingNews = listNews.find {
                                it.title == news.title && it.descriptionNews == news.descriptionNews && it.textNews == news.textNews
                            }
                            if (findingNews == null)
                                listNews.add(news)

                            val sourceNews =
                                sourceNewsFromCash.find { it.title == news.title && it.descriptionNews == news.descriptionNews && it.textNews == news.textNews }
                            if (sourceNews == null) {
                                val newsDomain = NewsToNewsDomain.convertNewsToNewsDomain(news)
                                articlesBySourceTableRepositoryInterface.addArticleBySource(
                                    newsDomain
                                )
                            }
                        }

                        var searchListNews: List<News> = listNews
                        if (search.isNotEmpty()) {
                            searchWordNews = search
                            searchListNews = searchListNews.filter {
                                (it.title != null && it.title.contains(search, true)) ||
                                        (it.descriptionNews != null && it.descriptionNews.contains(
                                            search,
                                            true
                                        )) ||
                                        (it.textNews != null && it.textNews.contains(search, true))
                            }
                        }

                        mutableNewsLiveData.postValue(searchListNews)
                    }
                })
        }
    }

    fun filterSources(search: String = searchWordSource, language: String = languageFilter) {
        viewModelScope.launch {
            searchWordSource = search
            val listSources = ArrayList<Source>()

            withContext(Dispatchers.IO) {
                var filterList = sourceTableRepositoryInterface.getAllSources().filter {
                    it.name.contains(search, true)
                }

                if (language.isNotEmpty())
                    filterList = filterList.filter {
                        it.language == language
                    }

                for (sourceDomain in filterList)
                    listSources.add(SourceDomainToSource.convertSourceDomainToSource(sourceDomain))
            }

            mutableSourcesLiveData.value = listSources
        }
    }

    fun filterSourceNews(
        search: String = searchWordNews,
        sortBy: String = sortByFilter,
        from: Long = fromDateFilter,
        to: Long = toDateFilter,
        source: String?,
    ) {
        viewModelScope.launch {
            searchWordNews = search

            val listSourceNews = ArrayList<News>()
            source?.let { source ->
                withContext(Dispatchers.IO) {
                    var filterList =
                        articlesBySourceTableRepositoryInterface.getAllArticlesBySource(source)
                            .filter {
                                (it.title != null && it.title.contains(search, true)) ||
                                        (it.descriptionNews != null && it.descriptionNews.contains(
                                            search,
                                            true
                                        )) ||
                                        (it.textNews != null && it.textNews.contains(search, true))
                            }

                    if (from > 0 && to > 0)
                        filterList = filterList.filter {
                            it.date != null && it.date >= from && it.date <= to
                        }

                    if (sortBy.isNotEmpty()) {
                        filterList = filterList.sortedBy {
                            it.date
                        }
                    }

                    for (newsDomain in filterList)
                        listSourceNews.add(NewsDomainToNews.convertNewsDomainToNews(newsDomain))
                }
            }

            mutableNewsLiveData.value = listSourceNews
        }
    }

    fun fillFiltersSources(): Int {
        var countFilters = 0
        val filtersState = FiltersFragment.filtersState
        if (filtersState != null)
            if (filtersState.language != null) {
                languageFilter = filtersState.language
                countFilters++
            }
        return countFilters
    }

    fun fillFiltersNews(isInternetAvailable: Boolean): Int {
        var countFilters = 0
        val filtersState = FiltersFragment.filtersState
        if (filtersState != null) {
            sortByFilter = filtersState.typeSort
            if (isInternetAvailable) {
                countFilters++
                if (filtersState.language != null) {
                    languageFilter = filtersState.language
                    countFilters++
                }
            }
            if (filtersState.fromDate != null && filtersState.toDate != null) {
                fromDateFilter = filtersState.fromDate
                toDateFilter = filtersState.toDate
                countFilters++
            }
        }
        return countFilters
    }

    private fun clearCashSources(allSourcesDomain: List<SourceDomain>): List<SourceDomain> {
        val allSources = ArrayList<SourceDomain>()
        for (sourceDomain in allSourcesDomain) {
            val days14 = Date().time - 14 * 1000 * 60 * 60 * 24
            sourceDomain.dateSaving?.let {
                if (it <= days14)
                    sourceTableRepositoryInterface.deleteSource(sourceDomain)
                else
                    allSources.add(sourceDomain)
            }
        }
        return allSources
    }

    private fun clearCashSourceNews(
        allNewsDomain: List<NewsDomain>,
        source: String,
    ): List<NewsDomain> {
        val allSourceNews = ArrayList<NewsDomain>()
        for (newsDomain in allNewsDomain) {
            val days14 = Date().time - 14 * 1000 * 60 * 60 * 24
            newsDomain.dateSaving?.let {
                if (it <= days14)
                    articlesBySourceTableRepositoryInterface.deleteArticleBySource(
                        newsDomain,
                        source
                    )
                else
                    allSourceNews.add(newsDomain)
            }
        }
        return allSourceNews
    }

    override fun onCleared() {
        super.onCleared()
        articlesDisposable?.dispose()
    }
}