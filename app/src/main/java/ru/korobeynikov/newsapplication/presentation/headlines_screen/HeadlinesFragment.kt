package ru.korobeynikov.newsapplication.presentation.headlines_screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.tabs.TabLayout
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentHeadlinesBinding
import ru.korobeynikov.newsapplication.di.App
import ru.korobeynikov.newsapplication.presentation.base.BaseFragment
import ru.korobeynikov.newsapplication.presentation.error_screen.ErrorFragment
import ru.korobeynikov.newsapplication.presentation.error_screen.ErrorState
import ru.korobeynikov.newsapplication.presentation.filter_screen.FiltersFragment
import ru.korobeynikov.newsapplication.presentation.news.News
import ru.korobeynikov.newsapplication.presentation.news.NewsAdapter
import ru.korobeynikov.newsapplication.presentation.news_screen.NewsFragment
import ru.korobeynikov.newsapplication.presentation.recycler_view.RecyclerViewTouchListener
import javax.inject.Inject

class HeadlinesFragment : Fragment(), HeadlinesView, BaseFragment {

    @Inject
    lateinit var presenter: HeadlinesPresenter

    private lateinit var progressBarHeadlines: ProgressBar
    private lateinit var binding: FragmentHeadlinesBinding
    lateinit var recyclerViewHeadlines: RecyclerView

    private var filtersCount = 0
    val searchHandler = Handler(Looper.getMainLooper())
    var searchRunnable = Runnable {}

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(it: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            searchHandler.removeCallbacks(searchRunnable)
            searchRunnable = Runnable {
                val color = if (binding.searchEditText.text.isNotEmpty())
                    resources.getColor(R.color.background, null)
                else 0
                if (isInternetAvailable(context))
                    when (binding.tabHeadlines.selectedTabPosition) {
                        0 -> {
                            presenter.resetList("general")
                            recyclerViewHeadlines.adapter = NewsAdapter(ArrayList(), color)
                            presenter.getNews("general", " ${text.toString()}")
                        }

                        1 -> {
                            presenter.resetList("business")
                            recyclerViewHeadlines.adapter = NewsAdapter(ArrayList(), color)
                            presenter.getNews("business", " ${text.toString()}")
                        }

                        2 -> {
                            presenter.resetList("traveling")
                            recyclerViewHeadlines.adapter = NewsAdapter(ArrayList(), color)
                            presenter.getNews("traveling", " ${text.toString()}")
                        }
                    }
                else {
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList(), color)
                    when (binding.tabHeadlines.selectedTabPosition) {
                        0 -> presenter.filterNewsWithoutInternet("general", text.toString())
                        1 -> presenter.filterNewsWithoutInternet("business", text.toString())
                        2 -> presenter.filterNewsWithoutInternet("traveling", text.toString())
                    }
                }
            }
            searchHandler.postDelayed(searchRunnable, 1000)
        }
    }

    @ExperimentalBadgeUtils
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHeadlinesBinding.inflate(layoutInflater)

        (activity?.application as App).appComponent.injectHeadlinesFragment(this)
        presenter.attachView(this)

        val tabHeadLines = binding.tabHeadlines
        tabHeadLines.tabIconTint = null

        var tab = tabHeadLines.newTab()
        tab.text = getText(R.string.headlines_page_general)
        tab.setIcon(R.drawable.headlines_page_general)
        tabHeadLines.addTab(tab)
        tab = tabHeadLines.newTab()
        tab.text = getText(R.string.headlines_page_business)
        tab.setIcon(R.drawable.headlines_page_business)
        tabHeadLines.addTab(tab)
        tab = tabHeadLines.newTab()
        tab.text = getText(R.string.headlines_page_traveling)
        tab.setIcon(R.drawable.headlines_page_traveling)
        tabHeadLines.addTab(tab)

        progressBarHeadlines = binding.progressBarHeadlines
        recyclerViewHeadlines = binding.recyclerViewHeadlines
        val layoutManager = LinearLayoutManager(activity)
        recyclerViewHeadlines.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerViewHeadlines.context, layoutManager.orientation)
        recyclerViewHeadlines.addItemDecoration(dividerItemDecoration)
        recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())

        filtersCount = presenter.fillFilters(isInternetAvailable(context))
        drawBatch()

        tabHeadLines.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                when (tab?.position) {
                    0 -> {
                        if (isInternetAvailable(context)) {
                            if (HeadlinesPresenter.listGeneral.isEmpty())
                                presenter.getNews("general")
                            else
                                showNews(HeadlinesPresenter.listGeneral, "general")
                        } else
                            presenter.filterNewsWithoutInternet("general")
                    }

                    1 -> {
                        if (isInternetAvailable(context)) {
                            if (HeadlinesPresenter.listBusiness.isEmpty())
                                presenter.getNews("business")
                            else
                                showNews(HeadlinesPresenter.listBusiness, "business")
                        } else
                            presenter.filterNewsWithoutInternet("business")
                    }

                    2 -> {
                        if (isInternetAvailable(context)) {
                            if (HeadlinesPresenter.listTraveling.isEmpty())
                                presenter.getNews("traveling")
                            else
                                showNews(HeadlinesPresenter.listTraveling, "traveling")
                        } else
                            presenter.filterNewsWithoutInternet("traveling")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        recyclerViewHeadlines.addOnScrollListener(object :
            HeadlinesPaginationScrollListener(layoutManager) {

            override fun isLastPage() = presenter.isLastPage

            override fun isLoading() = presenter.isLoading

            override fun loadMoreItems() {
                if (isInternetAvailable(context))
                    when (tabHeadLines.selectedTabPosition) {
                        0 -> presenter.getNews("general")
                        1 -> presenter.getNews("business")
                        2 -> presenter.getNews("traveling")
                    }
            }
        })

        recyclerViewHeadlines.addOnItemTouchListener(
            RecyclerViewTouchListener(requireContext(),
                object : RecyclerViewTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putParcelable(
                            "news",
                            (recyclerViewHeadlines.adapter as NewsAdapter).dataSet[position]
                        )
                        val newsFragment = NewsFragment()
                        newsFragment.arguments = bundle
                        val fragmentTransaction = parentFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, newsFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                })
        )

        binding.swipeRefreshLayoutHeadlines.setOnRefreshListener {
            binding.swipeRefreshLayoutHeadlines.isRefreshing = false
            if (HeadlinesPresenter.searchWord.isNotEmpty())
                goToSearch()
            else
                resetTabs(tabHeadLines)
        }

        binding.headlinesToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu_item -> goToSearch()
                R.id.clear_menu_item -> binding.searchEditText.text.clear()
                R.id.filter_menu_item -> {
                    val bundle = Bundle()
                    bundle.putString("source_screen", "headlines")
                    val filtersFragment = FiltersFragment()
                    filtersFragment.arguments = bundle
                    val fragmentTransaction = parentFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment_container, filtersFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
            false
        }

        binding.headlinesToolbar.setNavigationOnClickListener {
            binding.headlinesToolbar.menu.clear()
            binding.headlinesToolbar.inflateMenu(R.menu.main_menu)
            binding.headlinesToolbar.navigationIcon = null
            binding.searchEditText.visibility = View.GONE
            binding.searchEditText.removeTextChangedListener(textWatcher)
            binding.searchEditText.text.clear()
            HeadlinesPresenter.searchWord = ""
            binding.headlinesToolbarTitle.visibility = View.VISIBLE
            binding.tabHeadlines.visibility = View.VISIBLE
            drawBatch()
            resetTabs(tabHeadLines)
        }

        if (HeadlinesPresenter.searchWord.isNotEmpty())
            goToSearch()
        else if (filtersCount > 0) {
            if (isInternetAvailable(context)) {
                HeadlinesPresenter.clearAllLists()
                presenter.getNews("general")
            } else {
                presenter.filterNewsWithoutInternet("general")
            }
        } else {
            if (!isInternetAvailable(context))
                presenter.filterNewsWithoutInternet("general")
            else if (HeadlinesPresenter.listGeneral.isEmpty())
                presenter.getNews("general")
            else
                showNews(HeadlinesPresenter.listGeneral, "general")
        }

        return binding.root
    }

    private fun resetTabs(tabHeadLines: TabLayout) {
        if (isInternetAvailable(context))
            when (tabHeadLines.selectedTabPosition) {
                0 -> {
                    presenter.resetList("general")
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                    presenter.getNews("general")
                }

                1 -> {
                    presenter.resetList("business")
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                    presenter.getNews("business")
                }

                2 -> {
                    presenter.resetList("traveling")
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                    presenter.getNews("traveling")
                }
            }
        else
            when (tabHeadLines.selectedTabPosition) {
                0 -> {
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                    presenter.filterNewsWithoutInternet("general")
                }

                1 -> {
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                    presenter.filterNewsWithoutInternet("business")
                }

                2 -> {
                    recyclerViewHeadlines.adapter = NewsAdapter(ArrayList())
                    presenter.filterNewsWithoutInternet("traveling")
                }
            }
    }

    @ExperimentalBadgeUtils
    override fun showNews(listNews: List<News>, category: String) {
        val copyListNews = ArrayList<News>()
        for (news in listNews)
            copyListNews.add(
                News(
                    news.id,
                    news.title,
                    news.imageNews,
                    news.descriptionNews,
                    news.urlNews,
                    news.imageSource,
                    news.nameSource,
                    news.date,
                    news.textNews
                )
            )

        if (!isInternetAvailable(context) && copyListNews.isEmpty()) {
            showError(true)
        } else {
            val adapter = (recyclerViewHeadlines.adapter as NewsAdapter)
            val headlinesDiffUtilCallback =
                HeadlinesDiffUtilCallback(adapter.dataSet, copyListNews)
            val headlinesDiffResult = DiffUtil.calculateDiff(headlinesDiffUtilCallback)
            adapter.dataSet = copyListNews
            headlinesDiffResult.dispatchUpdatesTo(adapter)
        }
    }

    override fun showProgressBar(isVisible: Boolean) {
        if (isVisible)
            progressBarHeadlines.visibility = View.VISIBLE
        else
            progressBarHeadlines.visibility = View.GONE
    }

    @ExperimentalBadgeUtils
    override fun showError(isInternetError: Boolean) {
        val bundle = Bundle()
        bundle.putParcelable("errorState", ErrorState(isInternetError, "Headlines"))
        val errorFragment = ErrorFragment()
        errorFragment.arguments = bundle
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, errorFragment)
        fragmentTransaction.commit()
    }

    override fun isInternetAvailable(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val activeNetworkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    override fun goToSearch() {
        binding.headlinesToolbar.menu.clear()
        binding.headlinesToolbar.inflateMenu(R.menu.clear_menu)
        binding.headlinesToolbarTitle.visibility = View.GONE
        binding.tabHeadlines.visibility = View.GONE
        binding.headlinesToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.searchEditText.visibility = View.VISIBLE
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    @ExperimentalBadgeUtils
    override fun drawBatch() {
        if (filtersCount > 0) {
            val badge = BadgeDrawable.create(requireContext())
            badge.number = filtersCount
            BadgeUtils.attachBadgeDrawable(badge, binding.headlinesToolbar, R.id.filter_menu_item)
        }
    }
}