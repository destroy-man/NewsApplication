package ru.korobeynikov.newsapplication.presentation.base

interface BaseActivity<T> {
    fun getBinding(): T
}