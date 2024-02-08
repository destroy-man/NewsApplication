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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentSourcesBinding
import ru.korobeynikov.newsapplication.di.App
import ru.korobeynikov.newsapplication.presentation.base.BaseFragment
import ru.korobeynikov.newsapplication.presentation.error_screen.ErrorFragment
import ru.korobeynikov.newsapplication.presentation.error_screen.ErrorState
import ru.korobeynikov.newsapplication.presentation.filter_screen.FiltersFragment
import ru.korobeynikov.newsapplication.presentation.recycler_view.RecyclerViewTouchListener
import javax.inject.Inject

class SourcesFragment : Fragment(), BaseFragment {

    @Inject
    lateinit var sourceViewModelFactory: SourcesViewModelFactory

    private lateinit var sourceViewModel: SourceViewModel
    private lateinit var binding: FragmentSourcesBinding
    private var isSearchMode = false

    private var filtersCount = 0
    val searchHandler = Handler(Looper.getMainLooper())
    var searchRunnable = Runnable {}

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(it: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            searchHandler.removeCallbacks(searchRunnable)
            searchRunnable = Runnable {
                if (text.toString().isNotEmpty())
                    sourceViewModel.filterSources(text.toString())
                else
                    sourceViewModel.filterSources("")
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
        binding = FragmentSourcesBinding.inflate(layoutInflater)

        (activity?.application as App).appComponent.injectSourcesFragment(this)
        sourceViewModel =
            ViewModelProvider(this, sourceViewModelFactory)[SourceViewModel::class.java]
        val listSources = ArrayList<Source>()

        val recyclerViewSources = binding.recyclerViewSources
        val layoutManager = LinearLayoutManager(activity)
        recyclerViewSources.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerViewSources.context, layoutManager.orientation)
        recyclerViewSources.addItemDecoration(dividerItemDecoration)

        filtersCount = sourceViewModel.fillFiltersSources()
        drawBatch()

        sourceViewModel.sourcesLiveData.observe(viewLifecycleOwner) {
            listSources.clear()
            listSources.addAll(it)
            if (!isInternetAvailable(context) && it.isEmpty())
                showError(true)
            else {
                if (binding.searchEditText.text.isNotEmpty()) {
                    val color = resources.getColor(R.color.background, null)
                    recyclerViewSources.adapter = SourceAdapter(listSources, color)
                } else
                    recyclerViewSources.adapter = SourceAdapter(listSources)
            }
        }

        recyclerViewSources.addOnItemTouchListener(
            RecyclerViewTouchListener(requireContext(),
                object : RecyclerViewTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putParcelable("source", listSources[position])
                        val sourceNewsFragment = SourceNewsFragment()
                        sourceNewsFragment.arguments = bundle
                        val fragmentTransaction = parentFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, sourceNewsFragment)
                        fragmentTransaction.commit()
                    }
                })
        )

        binding.swipeRefreshLayoutSources.setOnRefreshListener {
            binding.swipeRefreshLayoutSources.isRefreshing = false
            if (isInternetAvailable(context)) {
                SourceViewModel.listSources.clear()
                sourceViewModel.getSourcesFromNetwork()
            }
        }

        sourceViewModel.errorLiveData.observe(viewLifecycleOwner) {
            showError(it)
        }

        binding.sourcesToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu_item -> goToSearch()
                R.id.clear_menu_item -> binding.searchEditText.text.clear()
                R.id.filter_menu_item -> {
                    val bundle = Bundle()
                    bundle.putString("source_screen", "sources")
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

        binding.sourcesToolbar.setNavigationOnClickListener {
            isSearchMode = false
            binding.sourcesToolbar.menu.clear()
            binding.sourcesToolbar.inflateMenu(R.menu.main_menu)
            binding.sourcesToolbar.navigationIcon = null
            binding.searchEditText.visibility = View.GONE
            binding.searchEditText.removeTextChangedListener(textWatcher)
            binding.searchEditText.text.clear()
            binding.sourcesToolbarTitle.visibility = View.VISIBLE
            SourceViewModel.searchWordSource = ""
            drawBatch()
            if (isInternetAvailable(context))
                sourceViewModel.getSourcesFromNetwork()
            else
                sourceViewModel.filterSources()
        }

        if (SourceViewModel.searchWordSource.isNotEmpty())
            goToSearch()
        else if (filtersCount > 0) {
            if (isInternetAvailable(context)) {
                SourceViewModel.listSources.clear()
                sourceViewModel.getSourcesFromNetwork()
            } else
                sourceViewModel.filterSources()
        } else {
            if (isInternetAvailable(context))
                sourceViewModel.getSourcesFromNetwork()
            else
                sourceViewModel.filterSources()
        }

        return binding.root
    }

    @ExperimentalBadgeUtils
    fun showError(isInternetError: Boolean) {
        val bundle = Bundle()
        bundle.putParcelable("errorState", ErrorState(isInternetError, "Sources"))
        val errorFragment = ErrorFragment()
        errorFragment.arguments = bundle
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, errorFragment)
        fragmentTransaction.commit()
    }

    override fun goToSearch() {
        isSearchMode = true
        binding.sourcesToolbar.menu.clear()
        binding.sourcesToolbar.inflateMenu(R.menu.clear_menu)
        binding.sourcesToolbarTitle.visibility = View.GONE
        binding.sourcesToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.searchEditText.visibility = View.VISIBLE
        binding.searchEditText.addTextChangedListener(textWatcher)
        binding.searchEditText.setText(SourceViewModel.searchWordSource)
    }

    @ExperimentalBadgeUtils
    override fun drawBatch() {
        if (filtersCount > 0) {
            val badge = BadgeDrawable.create(requireContext())
            badge.number = filtersCount
            BadgeUtils.attachBadgeDrawable(badge, binding.sourcesToolbar, R.id.filter_menu_item)
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