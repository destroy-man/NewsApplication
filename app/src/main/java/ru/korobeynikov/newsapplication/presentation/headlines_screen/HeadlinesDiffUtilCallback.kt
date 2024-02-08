package ru.korobeynikov.newsapplication.presentation.headlines_screen

import androidx.recyclerview.widget.DiffUtil
import ru.korobeynikov.newsapplication.presentation.news.News

class HeadlinesDiffUtilCallback(
    private val oldList: ArrayList<News>,
    private val newList: ArrayList<News>,
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}