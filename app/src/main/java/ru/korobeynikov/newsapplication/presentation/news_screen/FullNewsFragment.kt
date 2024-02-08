package ru.korobeynikov.newsapplication.presentation.news_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentFullNewsBinding
import ru.korobeynikov.newsapplication.di.App
import ru.korobeynikov.newsapplication.presentation.news.News
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModel
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModelFactory
import javax.inject.Inject

class FullNewsFragment : Fragment() {

    @Inject
    lateinit var newsViewModelFactory: NewsViewModelFactory

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var newsViewModel: NewsViewModel
    var news: News? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFullNewsBinding.inflate(layoutInflater)
        (activity?.application as App).appComponent.injectFullNewsFragment(this)
        newsViewModel = ViewModelProvider(this, newsViewModelFactory)[NewsViewModel::class.java]
        news = BundleCompat.getParcelable(requireArguments(), "news", News::class.java)
        news?.let { news ->
            binding.fullnewsToolbarTitle.text = news.title
            if (news.textNews != null)
                binding.textNews.text = news.textNews
            else {
                binding.emptyNewsImage.visibility = View.VISIBLE
                binding.emptyNewsText.visibility = View.VISIBLE
            }

            scope.launch {
                newsViewModel.getNews(news).collect {
                    if (it == null)
                        binding.toolbarFullNews.menu.getItem(0).isVisible = true
                    else
                        binding.toolbarFullNews.menu.getItem(1).isVisible = true
                }
            }

            binding.toolbarFullNews.setOnMenuItemClickListener {
                scope.launch {
                    when (it.itemId) {
                        R.id.save_menu_item -> {
                            newsViewModel.saveNewsToDB(news)
                            withContext(Dispatchers.Main) {
                                binding.toolbarFullNews.menu.getItem(0).isVisible = false
                                binding.toolbarFullNews.menu.getItem(1).isVisible = true
                            }
                        }

                        R.id.saved_menu_item -> {
                            newsViewModel.deleteNewsFromDB(news)
                            withContext(Dispatchers.Main) {
                                binding.toolbarFullNews.menu.getItem(1).isVisible = false
                                binding.toolbarFullNews.menu.getItem(0).isVisible = true
                            }
                        }
                    }
                }
                false
            }
        }

        binding.toolbarFullNews.setNavigationIcon(R.drawable.back_icon)
        binding.toolbarFullNews.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive)
            scope.cancel()
    }
}