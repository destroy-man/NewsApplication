package ru.korobeynikov.newsapplication.presentation.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.korobeynikov.newsapplication.R

class NewsAdapter(var dataSet: ArrayList<News>, private val colorBackground: Int = 0) :
    RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    class NewsHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val titleNews: TextView
        val imageNews: ImageView
        val imageSource: ImageView
        val nameSource: TextView

        init {
            imageNews = view.findViewById(R.id.imageNews)
            titleNews = view.findViewById(R.id.titleNews)
            imageSource = view.findViewById(R.id.imageSource)
            nameSource = view.findViewById(R.id.nameSource)
        }

        fun bind(news: News) {
            if (!news.imageNews.isNullOrEmpty())
                Picasso.get().load(news.imageNews).resize(1920, 1080).onlyScaleDown()
                    .placeholder(android.R.drawable.ic_menu_rotate)
                    .error(android.R.drawable.ic_delete)
                    .into(imageNews)
            else
                Picasso.get().load(android.R.drawable.ic_delete).into(imageNews)
            titleNews.text = news.title
            imageSource.setImageResource(news.imageSource ?: R.drawable.other_source_icon)
            nameSource.text = news.nameSource
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NewsHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.news_item, viewGroup, false)
        if (colorBackground != 0)
            view.setBackgroundColor(colorBackground)
        return NewsHolder(view)
    }

    override fun onBindViewHolder(viewHolder: NewsHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}