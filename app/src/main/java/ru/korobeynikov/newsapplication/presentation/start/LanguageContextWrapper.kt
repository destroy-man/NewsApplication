package ru.korobeynikov.newsapplication.presentation.start

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

class LanguageContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {

        fun wrap(context: Context, language: String): ContextWrapper {
            val config: Configuration = context.resources.configuration
            val sysLocale: Locale? = getSystemLocale(config)
            if (language != "" && sysLocale != null && sysLocale.language != language) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                setSystemLocale(config, locale)
            }
            val contextMutable = context.createConfigurationContext(config)
            return LanguageContextWrapper(contextMutable)
        }

        private fun getSystemLocale(config: Configuration): Locale? {
            return config.locales[0]
        }

        private fun setSystemLocale(config: Configuration, locale: Locale?) {
            config.setLocale(locale)
        }
    }
}