package ru.korobeynikov.newsapplication.di

import dagger.Component
import ru.korobeynikov.newsapplication.presentation.headlines_screen.HeadlinesFragment
import ru.korobeynikov.newsapplication.presentation.news_screen.FullNewsFragment
import ru.korobeynikov.newsapplication.presentation.news_screen.NewsFragment
import ru.korobeynikov.newsapplication.presentation.saved_screen.SavedFragment
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourceNewsFragment
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourcesFragment

@ApplicationScope
@Component(modules = [HeadlinesPresenterModule::class, NewsModule::class, SourceModule::class])
interface AppComponent {

    fun injectHeadlinesFragment(headlinesFragment: HeadlinesFragment)

    fun injectNewsFragment(newsFragment: NewsFragment)

    fun injectFullNewsFragment(fullNewsFragment: FullNewsFragment)

    fun injectSavedFragment(savedFragment: SavedFragment)

    fun injectSourcesFragment(sourcesFragment: SourcesFragment)

    fun injectSourceNewsFragment(sourceNewsFragment: SourceNewsFragment)
}