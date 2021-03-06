package com.lianda.movies.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lianda.movies.R
import com.lianda.movies.base.BaseViewHolder
import com.lianda.movies.databinding.ItemGenreBinding
import com.lianda.movies.domain.model.Genre

class GenreAdapter(
    private val context: Context,
    var data: List<Genre>,
    val onGenreClicked: ((genre: Genre) -> Unit)? = null
) :
    RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        return GenreViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    inner class GenreViewHolder(itemView: View) : BaseViewHolder<Genre>(itemView) {
        private val binding = ItemGenreBinding.bind(itemView)
        override fun bind(data: Genre) {
            with(itemView) {
                binding.tvGenre.text = data.name

                setOnClickListener {
                    onGenreClicked?.invoke(data)
                }
            }
        }
    }

    fun notifyDataAddOrUpdate(newData: List<Genre>) {
        data = newData
        notifyDataSetChanged()
    }
}