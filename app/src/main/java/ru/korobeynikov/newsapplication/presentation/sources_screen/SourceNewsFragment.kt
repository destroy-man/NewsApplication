package ru.korobeynikov.newsapplication.presentation.sources_screen

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
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentSourceNewsBinding
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

@ExperimentalBadgeUtils
class SourceNewsFragment : Fragment(), BaseFragment {

    @Inject
    lateinit var sourceViewModelFactory: SourcesViewModelFactory

    private lateinit var sourceViewModel: SourceViewModel
    private lateinit var binding: FragmentSourceNewsBinding
    private var source: Source? = null

    private var filtersCount = 0
    val searchHandler = Handler(Looper.getMainLooper())
    var searchRunnable = Runnable {}

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(it: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            searchHandler.removeCallbacks(searchRunnable)
            searchRunnable = Runnable {
                if (isInternetAvailable(context))
                    sourceViewModel.getNewsBySource(
                        source?.id,
                        source?.name,
                        search = text.toString()
                    )
                else
                    sourceViewModel.filterSourceNews(text.toString(), source = source?.name)
            }
            searchHandler.postDelayed(searchRunnable, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSourceNewsBinding.inflate(layoutInflater)
        source = BundleCompat.getParcelable(requireArguments(), "source", Source::class.java)
        binding.sourcesNewsToolbarTitle.text = source?.name

        (activity?.application as App).appComponent.injectSourceNewsFragment(this)
        sourceViewModel =
            ViewModelProvider(this, sourceViewModelFactory)[SourceViewModel::class.java]

        val listNews = ArrayList<News>()
        val recyclerViewSourcesNews = binding.recyclerViewSourcesNews
        val layoutManager = LinearLayoutManager(activity)
        recyclerViewSourcesNews.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerViewSourcesNews.context, layoutManager.orientation)
        recyclerViewSourcesNews.addItemDecoration(dividerItemDecoration)

        filtersCount = sourceViewModel.fillFiltersNews(isInternetAvailable(context))
        drawBatch()

        sourceViewModel.newsLiveData.observe(requireActivity()) {
            showNews(listNews, it)
        }

        recyclerViewSourcesNews.addOnItemTouchListener(
            RecyclerViewTouchListener(requireContext(),
                object :
                    RecyclerViewTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putParcelable("news", listNews[position])
                        val newsFragment = NewsFragment()
                        newsFragment.arguments = bundle
                        val fragmentTransaction = parentFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, newsFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                })
        )

        binding.swipeRefreshLayoutSourceNews.setOnRefreshListener {
            binding.swipeRefreshLayoutSourceNews.isRefreshing = false
            if (isInternetAvailable(context))
                sourceViewModel.getNewsBySource(source?.id, source?.name)
        }

        binding.sourcesNewsToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.sourcesNewsToolbar.setNavigationOnClickListener {
            if (!binding.searchEditText.isVisible) {
                SourceViewModel.listNews.clear()
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, SourcesFragment())
                fragmentTransaction.commit()
            } else {
                binding.sourcesNewsToolbar.menu.clear()
                binding.sourcesNewsToolbar.inflateMenu(R.menu.main_menu)
                binding.searchEditText.visibility = View.GONE
                binding.searchEditText.removeTextChangedListener(textWatcher)
                binding.searchEditText.text.clear()
                binding.sourcesNewsToolbarTitle.visibility = View.VISIBLE
                SourceViewModel.searchWordNews = ""
                drawBatch()
                if (isInternetAvailable(context))
                    sourceViewModel.getNewsBySource(source?.id, source?.name)
                else
                    sourceViewModel.filterSourceNews(source = source?.name)
            }
        }

        binding.sourcesNewsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu_item -> goToSearch()
                R.id.clear_menu_item -> binding.searchEditText.text.clear()
                R.id.filter_menu_item -> {
                    val bundle = Bundle()
                    bundle.putString("source_screen", "newsSource")
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

        sourceViewModel.errorLiveData.observe(viewLifecycleOwner) {
            showError(it)
        }

        if (SourceViewModel.searchWordNews.isNotEmpty())
            goToSearch()
        else if (filtersCount > 0)
            if (isInternetAvailable(context))
                sourceViewModel.getNewsBySource(source?.id, source?.name)
            else
                sourceViewModel.filterSourceNews(source = source?.name)
        else {
            if (!isInternetAvailable(context))
                sourceViewModel.filterSourceNews(source = source?.name)
            else if (SourceViewModel.listNews.isEmpty())
                sourceViewModel.getNewsBySource(source?.id, source?.name)
            else
                showNews(listNews, SourceViewModel.listNews)
        }

        return binding.root
    }

    private fun showNews(listNews: ArrayList<News>, newListNews: List<News>) {
        listNews.clear()
        listNews.addAll(newListNews)
        if (!isInternetAvailable(context) && listNews.isEmpty()) {
            showError(true)
        } else {
            if (SourceViewModel.searchWordNews.isNotEmpty()) {
                val color = resources.getColor(R.color.background, null)
                binding.recyclerViewSourcesNews.adapter = NewsAdapter(listNews, color)
            } else
                binding.recyclerViewSourcesNews.adapter = NewsAdapter(listNews)
        }
    }

    private fun showError(isInternetError: Boolean) {
        val bundle = Bundle()
        bundle.putParcelable("errorState", ErrorState(isInternetError, "SourceNews", source))
        val errorFragment = ErrorFragment()
        errorFragment.arguments = bundle
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, errorFragment)
        fragmentTransaction.commit()
    }

    override fun goToSearch() {
        binding.sourcesNewsToolbar.menu.clear()
        binding.sourcesNewsToolbar.inflateMenu(R.menu.clear_menu)
        binding.sourcesNewsToolbarTitle.visibility = View.GONE
        binding.searchEditText.visibility = View.VISIBLE
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    @ExperimentalBadgeUtils
    override fun drawBatch() {
        if (filtersCount > 0) {
            val badge = BadgeDrawable.create(requireContext())
            badge.number = filtersCount
            BadgeUtils.attachBadgeDrawable(badge, binding.sourcesNewsToolbar, R.id.filter_menu_item)
        }
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
}