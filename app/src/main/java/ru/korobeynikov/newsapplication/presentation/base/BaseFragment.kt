package ru.korobeynikov.newsapplication.presentation.base

import android.content.Context

interface BaseFragment {

    fun isInternetAvailable(context: Context?): Boolean

    fun goToSearch()

    fun drawBatch()
}