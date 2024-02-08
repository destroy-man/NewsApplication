package ru.korobeynikov.newsapplication.presentation.headlines_screen

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.domain.classes.ArticleByCategoryDomain
import ru.korobeynikov.newsapplication.domain.classes.ArticleDomain
import ru.korobeynikov.newsapplication.domain.repositories.ArticleByCategoryTableRepositoryInterface
import ru.korobeynikov.newsapplication.domain.repositories.ArticlesRepositoryInterface
import ru.korobeynikov.newsapplication.presentation.base.BasePresenter
import ru.korobeynikov.newsapplication.presentation.filter_screen.FiltersFragment
import ru.korobeynikov.newsapplication.presentation.mapper.ArticleByCategoryDomainToNews
import ru.korobeynikov.newsapplication.presentation.mapper.NewsToArticleByCategoryDomain
import ru.korobeynikov.newsapplication.presentation.news.News
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@InjectViewState
class HeadlinesPresenter(
    private val headlinesRepository: ArticlesRepositoryInterface,
    private val articleByCategoryTableRepositoryInterface: ArticleByCategoryTableRepositoryInterface,
) : MvpPresenter<HeadlinesView>(), BasePresenter {

    companion object {
        var listGeneral = ArrayList<News>()
        var listBusiness = ArrayList<News>()
        var listTraveling = ArrayList<News>()

        var pageGeneral = 1
        var pageBusiness = 1
        var pageTraveling = 1
        var isLastPageGeneral = false
        var isLastPageBusiness = false
        var isLastPageTraveling = false

        var searchWord = ""

        fun clearAllLists() {
            pageGeneral = 1
            isLastPageGeneral = false
            listGeneral.clear()
            pageBusiness = 1
            isLastPageBusiness = false
            listBusiness.clear()
            pageTraveling = 1
            isLastPageTraveling = false
            listTraveling.clear()
        }
    }

    var isLastPage = false
    var isLoading = false
    var headlinesDisposable: Disposable? = null

    private var languageFilter = ""
    private var sortByFilter = ""
    private var fromDateFilter = 0L
    private var toDateFilter = 0L

    fun getNews(
        category: String,
        search: String = searchWord,
        language: String = languageFilter,
        sortBy: String = sortByFilter,
        from: Long = fromDateFilter,
        to: Long = toDateFilter,
    ) {
        searchWord = search

        var fromDate = ""
        var toDate = ""
        if (from > 0 && to > 0) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            fromDate = formatter.format(Date(from))
            toDate = formatter.format(Date(to))
        }
        when (category) {
            "general" -> {
                if (pageGeneral == 1) {
                    listGeneral.clear()
                    isLastPage = isLastPageGeneral
                }
                if (!isLastPageGeneral) {
                    isLoading = true
                    viewState.showProgressBar(true)
                    getNewsFromNet(
                        category,
                        pageGeneral,
                        listGeneral,
                        search,
                        language,
                        sortBy,
                        fromDate,
                        toDate
                    )
                    pageGeneral++
                }
            }

            "business" -> {
                if (pageBusiness == 1) {
                    listBusiness.clear()
                    isLastPage = isLastPageBusiness
                }
                if (!isLastPageBusiness) {
                    isLoading = true
                    viewState.showProgressBar(true)
                    getNewsFromNet(
                        category,
                        pageBusiness,
                        listBusiness,
                        search,
                        language,
                        sortBy,
                        fromDate,
                        toDate
                    )
                    pageBusiness++
                }
            }

            "traveling" -> {
                if (pageTraveling == 1) {
                    listTraveling.clear()
                    isLastPage = isLastPageTraveling
                }
                if (!isLastPageTraveling) {
                    isLoading = true
                    viewState.showProgressBar(true)
                    getNewsFromNet(
                        category,
                        pageTraveling,
                        listTraveling,
                        search,
                        language,
                        sortBy,
                        fromDate,
                        toDate
                    )
                    pageTraveling++
                }
            }
        }
    }

    private fun getNewsFromNet(
        category: String,
        page: Int,
        listNews: ArrayList<News>,
        search: String,
        language: String,
        sortBy: String,
        from: String,
        to: String,
    ) {
        headlinesRepository.getArticles(category, page, search, language, sortBy, from, to)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<ArticleDomain>> {
                override fun onSubscribe(d: Disposable) {
                    headlinesDisposable = d
                }

                override fun onError(e: Throwable) {
                    viewState.showError(e is IOException)
                }

                override fun onSuccess(articles: List<ArticleDomain>) {
                    for ((id, article) in articles.withIndex()) {
                        val idImageSource = when (article.sourceNews?.name) {
                            "ABC News" -> R.drawable.abc_news_icon
                            "CNBC" -> R.drawable.cnbc_icon
                            "BBC News" -> R.drawable.bbc_news_icon
                            "Reuters" -> R.drawable.reuters_icon
                            "YouTube" -> R.drawable.youtube_icon
                            else -> R.drawable.other_source_icon
                        }
                        listNews.add(
                            News(
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
                        )
                    }
                    if (articles.size < 20) {
                        isLastPage = true
                        when (category) {
                            "general" -> isLastPageGeneral = true
                            "business" -> isLastPageBusiness = true
                            "traveling" -> isLastPageTraveling = true
                        }
                    }
                    writeArticlesByCategoryToCash(listNews, category)
                    viewState.showNews(listNews, category)
                    viewState.showProgressBar(false)
                    isLoading = false
                }
            })
    }

    fun filterNewsWithoutInternet(
        category: String,
        search: String = searchWord,
        sortBy: String = sortByFilter,
        from: Long = fromDateFilter,
        to: Long = toDateFilter,
    ) {
        searchWord = search
        filterCategory(category, search, from, to, sortBy)
    }

    private fun filterCategory(
        category: String,
        search: String,
        from: Long,
        to: Long,
        sortBy: String,
    ) {
        articleByCategoryTableRepositoryInterface.getAllArticlesByCategory(category)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<ArticleByCategoryDomain>> {
                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    viewState.showError(false)
                }

                override fun onSuccess(listArticles: List<ArticleByCategoryDomain>) {
                    var filterList = listArticles.filter {
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

                    val listNews = ArrayList<News>()
                    for (articleByCategory in filterList)
                        listNews.add(
                            ArticleByCategoryDomainToNews.convertArticleByCategoryDomainToNews(
                                articleByCategory
                            )
                        )
                    viewState.showNews(listNews, category)
                    when (category) {
                        "general" -> isLastPageGeneral = true
                        "business" -> isLastPageBusiness = true
                        "traveling" -> isLastPageTraveling = true
                    }
                    viewState.showProgressBar(false)
                    isLoading = false
                    isLastPage = true
                }
            })
    }

    fun resetList(category: String) {
        when (category) {
            "general" -> {
                pageGeneral = 1
                isLastPageGeneral = false
            }

            "business" -> {
                pageBusiness = 1
                isLastPageBusiness = false
            }

            "traveling" -> {
                pageTraveling = 1
                isLastPageTraveling = false
            }
        }
        isLastPage = false
    }

    fun writeArticlesByCategoryToCash(listNews: List<News>, category: String) {
        articleByCategoryTableRepositoryInterface.getAllArticlesByCategory(category)
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<List<ArticleByCategoryDomain>> {
                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    viewState.showError(false)
                }

                override fun onSuccess(listArticles: List<ArticleByCategoryDomain>) {
                    var articlesFromCash = listArticles
                    articlesFromCash = clearCash(articlesFromCash, category)
                    for (news in listNews) {
                        val findingNews = articlesFromCash.find {
                            it.title == news.title && it.descriptionNews == news.descriptionNews
                                    && it.textNews == news.textNews && it.nameSource == news.nameSource
                        }
                        if (findingNews == null) {
                            val articleByCategoryDomain =
                                NewsToArticleByCategoryDomain.convertNewsToArticleByCategoryDomain(
                                    news,
                                    category
                                )
                            articleByCategoryTableRepositoryInterface.addArticleByCategory(
                                articleByCategoryDomain
                            )
                        }
                    }
                }
            })
    }

    private fun clearCash(
        allArticlesByCategoryDomain: List<ArticleByCategoryDomain>,
        category: String,
    ): List<ArticleByCategoryDomain> {
        val allArticles = ArrayList<ArticleByCategoryDomain>()
        for (articleDomain in allArticlesByCategoryDomain) {
            val days14 = Date().time - 14 * 1000 * 60 * 60 * 24
            articleDomain.dateSaving?.let {
                if (it <= days14)
                    articleByCategoryTableRepositoryInterface.deleteArticleByCategory(
                        articleDomain,
                        category
                    )
                else
                    allArticles.add(articleDomain)
            }
        }
        return allArticles
    }

    override fun fillFilters(isInternetAvailable: Boolean): Int {
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

    override fun onDestroy() {
        super.onDestroy()
        headlinesDisposable?.dispose()
    }
}