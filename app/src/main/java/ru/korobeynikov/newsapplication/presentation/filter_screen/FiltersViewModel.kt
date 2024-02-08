package ru.korobeynikov.newsapplication.presentation.filter_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FiltersViewModel : ViewModel() {

    private val mutableLiveDataFilters = MutableLiveData<FiltersState>()
    val liveData: LiveData<FiltersState> = mutableLiveDataFilters

    fun loadData(data: FiltersState) {
        mutableLiveDataFilters.value = data
    }

    fun initFiltersState() {
        mutableLiveDataFilters.value = FiltersState(
            "New",
            null,
            null,
            null
        )
    }

    fun actionChangeLanguage(language: String) {
        val filtersState = liveData.value
        if (filtersState != null)
            mutableLiveDataFilters.value = FiltersState(
                filtersState.typeSort,
                filtersState.fromDate,
                filtersState.toDate,
                language
            )
    }

    fun actionChangeTypeSort(typeSort: String) {
        val filtersState = liveData.value
        if (filtersState != null)
            mutableLiveDataFilters.value = FiltersState(
                typeSort,
                filtersState.fromDate,
                filtersState.toDate,
                filtersState.language
            )
    }

    fun actionChangeDate(fromDate: Long, toDate: Long) {
        val filtersState = liveData.value
        if (filtersState != null)
            mutableLiveDataFilters.value = FiltersState(
                filtersState.typeSort,
                fromDate,
                toDate,
                filtersState.language
            )
    }
}