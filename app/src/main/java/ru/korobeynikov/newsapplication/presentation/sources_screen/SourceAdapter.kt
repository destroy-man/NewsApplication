package ru.korobeynikov.newsapplication.presentation.sources_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.korobeynikov.newsapplication.R

class SourceAdapter(private var dataSet: List<Source>, private val colorBackground: Int = 0) :
    RecyclerView.Adapter<SourceAdapter.SourceHolder>() {

    class SourceHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textSource: TextView
        val imageSource: ImageView
        val nameSource: TextView

        init {
            imageSource = view.findViewById(R.id.imageSource)
            nameSource = view.findViewById(R.id.nameSource)
            textSource = view.findViewById(R.id.textSource)
        }

        fun bind(source: Source) {
            imageSource.setImageResource(source.image)
            nameSource.text = source.name
            textSource.text = source.description
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SourceHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.source_item, viewGroup, false)
        if (colorBackground != 0)
            view.setBackgroundColor(colorBackground)
        return SourceHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SourceHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}