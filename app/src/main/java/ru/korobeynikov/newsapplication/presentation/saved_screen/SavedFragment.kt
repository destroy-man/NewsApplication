package ru.korobeynikov.newsapplication.presentation.saved_screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentSavedBinding
import ru.korobeynikov.newsapplication.di.App
import ru.korobeynikov.newsapplication.presentation.base.BaseFragment
import ru.korobeynikov.newsapplication.presentation.filter_screen.FiltersFragment
import ru.korobeynikov.newsapplication.presentation.news.News
import ru.korobeynikov.newsapplication.presentation.news.NewsAdapter
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModel
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModelFactory
import ru.korobeynikov.newsapplication.presentation.news_screen.NewsFragment
import ru.korobeynikov.newsapplication.presentation.recycler_view.RecyclerViewTouchListener
import javax.inject.Inject

class SavedFragment : Fragment(), BaseFragment {

    @Inject
    lateinit var newsViewModelFactory: NewsViewModelFactory

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentSavedBinding
    lateinit var listSaved: ArrayList<News>

    private var filtersCount = 0
    val searchHandler = Handler(Looper.getMainLooper())
    var searchRunnable = Runnable {}

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(it: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            searchHandler.removeCallbacks(searchRunnable)
            searchRunnable = Runnable {
                Log.d("myLogs", "aaa")
                scope.launch {
                    newsViewModel.getFilterNews(text.toString()).collect {
                        listSaved = it
                        withContext(Dispatchers.Main) {
                            if (NewsViewModel.searchWord.isNotEmpty()) {
                                val color = resources.getColor(R.color.background, null)
                                binding.recyclerViewSaved.adapter = NewsAdapter(listSaved, color)
                            } else
                                binding.recyclerViewSaved.adapter = NewsAdapter(listSaved)
                        }
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
        binding = FragmentSavedBinding.inflate(layoutInflater)

        (activity?.application as App).appComponent.injectSavedFragment(this)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]

        val recyclerViewSaved = binding.recyclerViewSaved
        val layoutManager = LinearLayoutManager(activity)
        recyclerViewSaved.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerViewSaved.context, layoutManager.orientation)
        recyclerViewSaved.addItemDecoration(dividerItemDecoration)

        filtersCount = newsViewModel.fillFilters()
        drawBatch()

        recyclerViewSaved.addOnItemTouchListener(
            RecyclerViewTouchListener(requireContext(),
                object : RecyclerViewTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putParcelable("news", listSaved[position])
                        val newsFragment = NewsFragment()
                        newsFragment.arguments = bundle
                        val fragmentTransaction = parentFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, newsFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                })
        )

        binding.swipeRefreshLayoutSaved.setOnRefreshListener {
            binding.swipeRefreshLayoutSaved.isRefreshing = false
            if (NewsViewModel.searchWord.isNotEmpty())
                goToSearch()
            else
                showNews()
        }

        binding.savedToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu_item -> goToSearch()
                R.id.clear_menu_item -> binding.searchEditText.text.clear()
                R.id.filter_menu_item -> {
                    val bundle = Bundle()
                    bundle.putString("source_screen", "saved")
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

        binding.savedToolbar.setNavigationOnClickListener {
            binding.savedToolbar.menu.clear()
            binding.savedToolbar.inflateMenu(R.menu.main_menu)
            binding.savedToolbar.navigationIcon = null
            binding.searchEditText.visibility = View.GONE
            binding.searchEditText.removeTextChangedListener(textWatcher)
            binding.searchEditText.text.clear()
            binding.savedToolbarTitle.visibility = View.VISIBLE
            NewsViewModel.searchWord = ""
            drawBatch()
            showNews()
        }

        if (NewsViewModel.searchWord.isNotEmpty())
            goToSearch()
        else if (filtersCount > 0) {
            scope.launch {
                newsViewModel.getFilterNews().collect {
                    listSaved = it
                    withContext(Dispatchers.Main) {
                        if (NewsViewModel.searchWord.isNotEmpty()) {
                            val color = resources.getColor(R.color.background, null)
                            binding.recyclerViewSaved.adapter = NewsAdapter(listSaved, color)
                        } else
                            binding.recyclerViewSaved.adapter = NewsAdapter(listSaved)
                    }
                }
            }
        } else
            showNews()

        return binding.root
    }

    private fun showNews() {
        scope.launch {
            newsViewModel.getAllNews().collect {
                listSaved = it
                withContext(Dispatchers.Main) {
                    binding.recyclerViewSaved.adapter = NewsAdapter(listSaved)
                }
            }
        }
    }

    override fun goToSearch() {
        binding.savedToolbar.menu.clear()
        binding.savedToolbar.inflateMenu(R.menu.clear_menu)
        binding.savedToolbarTitle.visibility = View.GONE
        binding.savedToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.searchEditText.visibility = View.VISIBLE
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    @ExperimentalBadgeUtils
    override fun drawBatch() {
        if (filtersCount > 0) {
            val badge = BadgeDrawable.create(requireContext())
            badge.number = filtersCount
            BadgeUtils.attachBadgeDrawable(badge, binding.savedToolbar, R.id.filter_menu_item)
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

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive)
            scope.cancel()
    }
}