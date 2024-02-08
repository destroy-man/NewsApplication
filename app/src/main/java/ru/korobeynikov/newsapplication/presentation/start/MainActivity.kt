package ru.korobeynikov.newsapplication.presentation.start

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.ActivityMainBinding
import ru.korobeynikov.newsapplication.presentation.base.BaseActivity
import ru.korobeynikov.newsapplication.presentation.headlines_screen.HeadlinesFragment
import ru.korobeynikov.newsapplication.presentation.headlines_screen.HeadlinesPresenter
import ru.korobeynikov.newsapplication.presentation.news.NewsViewModel
import ru.korobeynikov.newsapplication.presentation.saved_screen.SavedFragment
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourceViewModel
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourcesFragment

class MainActivity : AppCompatActivity(), BaseActivity<ActivityMainBinding> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = getBinding()
        setContentView(binding.root)
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnItemSelectedListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            when (it.title) {
                "Headlines" -> {
                    fragmentTransaction.replace(
                        R.id.fragment_container,
                        HeadlinesFragment()
                    )
                    SourceViewModel.searchWordSource = ""
                    SourceViewModel.searchWordNews = ""
                    NewsViewModel.searchWord = ""
                }

                "Saved" -> {
                    fragmentTransaction.replace(R.id.fragment_container, SavedFragment())
                    HeadlinesPresenter.searchWord = ""
                    HeadlinesPresenter.clearAllLists()
                    SourceViewModel.searchWordSource = ""
                    SourceViewModel.searchWordNews = ""
                }

                "Sources" -> {
                    fragmentTransaction.replace(R.id.fragment_container, SourcesFragment())
                    HeadlinesPresenter.searchWord = ""
                    HeadlinesPresenter.clearAllLists()
                    NewsViewModel.searchWord = ""
                }
            }
            fragmentTransaction.commit()
            return@setOnItemSelectedListener true
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let {
            LanguageContextWrapper.wrap(it, "en")
        })
    }

    override fun getBinding() = ActivityMainBinding.inflate(layoutInflater)
}