package ru.korobeynikov.newsapplication.presentation.filter_screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentFilterBinding
import ru.korobeynikov.newsapplication.presentation.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FiltersFragment : Fragment(), MviView, BaseFragment {

    companion object {
        var prevFilterState: FiltersState? = null
        var filtersState: FiltersState? = null
    }

    private lateinit var filtersViewModel: FiltersViewModel
    private var isInitRender = true
    lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater)

        val sourceScreen = arguments?.getString("source_screen")
        if (sourceScreen != null) {
            when (sourceScreen) {
                "headlines", "newsSource" ->
                    if (!isInternetAvailable(context))
                        hideAllExceptDate()

                "sources" -> {
                    binding.sortPopularButton.visibility = View.GONE
                    binding.sortNewButton.visibility = View.GONE
                    binding.sortRelevantButton.visibility = View.GONE

                    binding.textDate.visibility = View.GONE
                    binding.textSelectDate.visibility = View.GONE
                    binding.btnChooseDate.visibility = View.GONE

                    val layoutParams =
                        binding.textSelectedLanguage.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams.marginStart = convertDpToPx(20)
                    layoutParams.topMargin = convertDpToPx(20)
                    binding.textSelectedLanguage.layoutParams = layoutParams
                }

                "saved" -> hideAllExceptDate()
            }
        }

        filtersViewModel = ViewModelProvider(this)[FiltersViewModel::class.java]
        filtersViewModel.liveData.observe(viewLifecycleOwner) {
            if (isInitRender)
                initRender(it)
            else
                render(it)
        }
        filtersState?.let {
            filtersViewModel.loadData(it)
        } ?: filtersViewModel.initFiltersState()

        binding.btnRussianLanguage.setOnClickListener {
            filtersViewModel.actionChangeLanguage("ru")
        }
        binding.btnEnglishLanguage.setOnClickListener {
            filtersViewModel.actionChangeLanguage("en")
        }
        binding.btnGermanLanguage.setOnClickListener {
            filtersViewModel.actionChangeLanguage("de")
        }

        binding.sortPopularButton.setOnClickListener {
            filtersViewModel.actionChangeTypeSort("popularity")
        }
        binding.sortNewButton.setOnClickListener {
            filtersViewModel.actionChangeTypeSort("publishedAt")
        }
        binding.sortRelevantButton.setOnClickListener {
            filtersViewModel.actionChangeTypeSort("relevancy")
        }

        binding.btnChooseDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                .setTitleText("Select date")
                .setSelection(Pair(filtersState?.fromDate, filtersState?.toDate))
                .setPositiveButtonText("Ok")
                .setNegativeButtonText("Cancel")
                .build()
            datePicker.show(parentFragmentManager, "")
            datePicker.addOnPositiveButtonClickListener {
                filtersViewModel.actionChangeDate(it.first, it.second)
            }
        }

        binding.filterToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.filterToolbar.setNavigationOnClickListener {
            filtersState = prevFilterState
            parentFragmentManager.popBackStack()
        }

        binding.filterToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.apply_menu_item -> {
                    prevFilterState = filtersState
                    parentFragmentManager.popBackStack()
                }
            }
            false
        }

        return binding.root
    }

    override fun initRender(state: FiltersState) {
        changeLanguage(state.language)
        changeTypeSort(state.typeSort)
        changeDate(state.fromDate, state.toDate)
        filtersState = state
        isInitRender = false
    }

    override fun render(state: FiltersState) {
        if (state.language != filtersState?.language)
            changeLanguage(state.language)
        if (state.typeSort != filtersState?.typeSort)
            changeTypeSort(state.typeSort)
        if (state.fromDate != filtersState?.fromDate || state.toDate != filtersState?.toDate)
            changeDate(state.fromDate, state.toDate)
        filtersState = state
    }

    private fun changeLanguage(language: String?) {
        when (language) {
            "ru" -> {
                binding.btnRussianLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.selected_button,
                        null
                    )
                )
                binding.btnRussianLanguage.strokeWidth = 0
                binding.btnEnglishLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.btnEnglishLanguage.strokeWidth = convertDpToPx(1)
                binding.btnGermanLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.btnGermanLanguage.strokeWidth = convertDpToPx(1)
            }

            "en" -> {
                binding.btnEnglishLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.selected_button,
                        null
                    )
                )
                binding.btnEnglishLanguage.strokeWidth = 0
                binding.btnRussianLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.btnRussianLanguage.strokeWidth = convertDpToPx(1)
                binding.btnGermanLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.btnGermanLanguage.strokeWidth = convertDpToPx(1)
            }

            "de" -> {
                binding.btnGermanLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.selected_button,
                        null
                    )
                )
                binding.btnGermanLanguage.strokeWidth = 0
                binding.btnRussianLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.btnRussianLanguage.strokeWidth = convertDpToPx(1)
                binding.btnEnglishLanguage.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                binding.btnEnglishLanguage.strokeWidth = convertDpToPx(1)
            }
        }
    }

    private fun changeTypeSort(typeSort: String?) {
        when (typeSort) {
            "popularity" -> {
                binding.sortPopularButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.apply_icon_black,
                    0,
                    0,
                    0
                )
                binding.sortPopularButton.isChecked = true
                binding.sortPopularButton.compoundDrawablePadding = convertDpToPx(10)
                binding.sortNewButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding.sortRelevantButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            "publishedAt" -> {
                binding.sortNewButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.apply_icon_black,
                    0,
                    0,
                    0
                )
                binding.sortNewButton.isChecked = true
                binding.sortNewButton.compoundDrawablePadding = convertDpToPx(10)
                binding.sortPopularButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding.sortRelevantButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            "relevancy" -> {
                binding.sortRelevantButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.apply_icon_black,
                    0,
                    0,
                    0
                )
                binding.sortRelevantButton.isChecked = true
                binding.sortRelevantButton.compoundDrawablePadding = convertDpToPx(10)
                binding.sortPopularButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding.sortNewButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    private fun changeDate(fromDate: Long?, toDate: Long?) {
        if (fromDate != null && toDate != null) {
            val formatterFromDate = SimpleDateFormat("MMM dd", Locale.US)
            val formatterToDate = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val date = getString(
                R.string.filter_date,
                formatterFromDate.format(Date(fromDate)),
                formatterToDate.format(Date(toDate))
            )
            binding.textSelectDate.text = date
            binding.textSelectDate.setTextColor(resources.getColor(R.color.dark_background, null))
            binding.btnChooseDate.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.background_calendar_selected,
                null
            )
            binding.btnChooseDate.setImageResource(R.drawable.calendar_filled_icon)

        }
    }

    private fun convertDpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()

    private fun hideAllExceptDate() {
        binding.textSelectedLanguage.visibility = View.GONE
        binding.btnRussianLanguage.visibility = View.GONE
        binding.btnEnglishLanguage.visibility = View.GONE
        binding.btnGermanLanguage.visibility = View.GONE

        binding.sortPopularButton.visibility = View.GONE
        binding.sortNewButton.visibility = View.GONE
        binding.sortRelevantButton.visibility = View.GONE

        var layoutParams =
            binding.btnChooseDate.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.marginEnd = convertDpToPx(40)
        layoutParams.topMargin = convertDpToPx(20)
        binding.btnChooseDate.layoutParams = layoutParams

        layoutParams =
            binding.textDate.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.marginStart = convertDpToPx(40)
        binding.textDate.layoutParams = layoutParams
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

    override fun goToSearch() {}

    override fun drawBatch() {}
}