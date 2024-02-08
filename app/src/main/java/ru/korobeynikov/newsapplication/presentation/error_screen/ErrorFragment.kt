package ru.korobeynikov.newsapplication.presentation.error_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import com.google.android.material.badge.ExperimentalBadgeUtils
import ru.korobeynikov.newsapplication.R
import ru.korobeynikov.newsapplication.databinding.FragmentErrorBinding
import ru.korobeynikov.newsapplication.presentation.headlines_screen.HeadlinesFragment
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourceNewsFragment
import ru.korobeynikov.newsapplication.presentation.sources_screen.SourcesFragment

@ExperimentalBadgeUtils
class ErrorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentErrorBinding.inflate(layoutInflater)

        val errorState =
            BundleCompat.getParcelable(requireArguments(), "errorState", ErrorState::class.java)
        if (errorState != null) {
            if (!errorState.isInternetError) {
                binding.errorToolbar.setNavigationIcon(R.drawable.back_icon)
                binding.errorText.text = getText(R.string.other_error)
            }
            when (errorState.sourceError) {
                "Headlines" -> binding.errorToolbarTitle.text = getText(R.string.top_text_headlines)
                "Sources" -> binding.errorToolbarTitle.text = getText(R.string.top_text_sources)
                "SourceNews" -> binding.errorToolbarTitle.text = errorState.sourceNews?.name
            }
        }

        binding.btnRefresh.setOnClickListener {
            refresh(errorState)
        }

        binding.errorToolbar.setNavigationOnClickListener {
            refresh(errorState)
        }

        return binding.root
    }

    private fun refresh(errorState: ErrorState?) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        when (errorState?.sourceError) {
            "Headlines" -> fragmentTransaction.replace(R.id.fragment_container, HeadlinesFragment())
            "Sources" -> fragmentTransaction.replace(R.id.fragment_container, SourcesFragment())
            "SourceNews" -> {
                val bundle = Bundle()
                bundle.putParcelable("source", errorState.sourceNews)
                val sourceNewsFragment = SourceNewsFragment()
                sourceNewsFragment.arguments = bundle
                fragmentTransaction.replace(R.id.fragment_container, sourceNewsFragment)
            }
        }
        fragmentTransaction.commit()
    }
}