package ru.korobeynikov.newsapplication.presentation.news_screen

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentNewsBinding
import ru.korobeynikov.newsapplication.di.App
import ru.korobeynikov.newsapplication.presentation.news.News
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModel
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class NewsFragment : Fragment() {

    @Inject
    lateinit var newsViewModelFactory: NewsViewModelFactory

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentNewsBinding
    var news: News? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewsBinding.inflate(layoutInflater)
        (activity?.application as App).appComponent.injectNewsFragment(this)
        newsViewModel = ViewModelProvider(this, newsViewModelFactory)[NewsViewModel::class.java]

        news = BundleCompat.getParcelable(requireArguments(), "news", News::class.java)
        news?.let { news ->
            val imageNews = binding.imageNews
            if (!news.imageNews.isNullOrEmpty())
                Picasso.get().load(news.imageNews).resize(1920, 1080).onlyScaleDown()
                    .placeholder(android.R.drawable.ic_menu_rotate)
                    .error(android.R.drawable.ic_delete)
                    .into(imageNews)
            else
                Picasso.get().load(android.R.drawable.ic_delete).into(imageNews)

            val formatter = SimpleDateFormat("MMM dd, yyyy | HH:mm a", Locale.US)
            val date = formatter.format(Date(news.date ?: 0L))
            binding.textTitle.text =
                getString(R.string.description_news, news.title, date, news.nameSource)
            binding.textNews.text = news.textNews

            val textNews = news.textNews
            if (textNews != null) {
                if (textNews.last() == '…') {
                    val startIndex = textNews.lastIndexOf('.')
                    val endIndex = textNews.lastIndex
                    setLinkText(textNews, startIndex, endIndex)
                } else if (textNews.last() == '.') {
                    val last3symbols = textNews.substring(textNews.lastIndex - 2)
                    val newText = if (last3symbols == "...")
                        textNews.substring(0, textNews.lastIndex - 2)
                    else
                        textNews.substring(0, textNews.lastIndex)
                    val startIndex = newText.lastIndexOf('.')
                    val endIndex = textNews.lastIndex
                    setLinkText(textNews, startIndex, endIndex)
                } else {
                    val startIndex = textNews.lastIndexOf('.')
                    val endIndex = textNews.lastIndex
                    setLinkText(textNews, startIndex, endIndex)
                }
            } else {
                binding.emptyNewsImage.visibility = View.VISIBLE
                binding.emptyNewsText.visibility = View.VISIBLE
            }

            scope.launch {
                newsViewModel.getNews(news).collect {
                    withContext(Dispatchers.Main) {
                        if (it == null)
                            binding.saveToolbar.menu.getItem(0).isVisible = true
                        else
                            binding.saveToolbar.menu.getItem(1).isVisible = true
                    }
                }
            }

            binding.saveToolbar.setOnMenuItemClickListener {
                scope.launch {
                    when (it.itemId) {
                        R.id.save_menu_item -> {
                            newsViewModel.saveNewsToDB(news)
                            withContext(Dispatchers.Main) {
                                binding.saveToolbar.menu.getItem(0).isVisible = false
                                binding.saveToolbar.menu.getItem(1).isVisible = true
                            }
                        }

                        R.id.saved_menu_item -> {
                            newsViewModel.deleteNewsFromDB(news)
                            withContext(Dispatchers.Main) {
                                binding.saveToolbar.menu.getItem(1).isVisible = false
                                binding.saveToolbar.menu.getItem(0).isVisible = true
                            }
                        }
                    }
                }
                false
            }
        }

        binding.saveToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.saveToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val newsToolbar = binding.newsToolbar
        val appBarNews = binding.appBarNews
        var isShow = true
        var scrollRange = -1
        appBarNews.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollRange == -1)
                scrollRange = appBarLayout.totalScrollRange
            if (scrollRange + verticalOffset == 0) {
                newsToolbar.title = news?.title
                isShow = true
            } else if (isShow) {
                newsToolbar.title = " "
                isShow = false
            }
        }

        return binding.root
    }

    private fun setLinkText(text: String, startIndex: Int, endIndex: Int) {
        val spanText = SpannableString(text)
        val clickSpanText = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val bundle = Bundle()
                bundle.putParcelable("news", news)
                val fragFullNews = FullNewsFragment()
                fragFullNews.arguments = bundle
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragFullNews)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
        spanText.setSpan(
            clickSpanText,
            startIndex + 1,
            endIndex + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textNews.text = spanText
        binding.textNews.movementMethod = LinkMovementMethod.getInstance()
        binding.textNews.highlightColor = Color.TRANSPARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive)
            scope.cancel()
    }
}