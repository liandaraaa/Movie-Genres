package com.lianda.movies.presentation.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lianda.movies.R
import com.lianda.movies.base.BaseEndlessRecyclerViewAdapter
import com.lianda.movies.base.BaseViewHolder
import com.lianda.movies.databinding.ItemReviewBinding
import com.lianda.movies.domain.model.Review
import kotlinx.android.synthetic.main.item_review.view.*

class ReviewAdapter(override val context: Context, var data: MutableList<Review>, val isPreview:Boolean = false) :
    BaseEndlessRecyclerViewAdapter<Review>(context, data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Review> {
        return if (viewType == VIEW_TYPE_ITEM) {
            ReviewViewHolder(getView(parent, viewType))
        } else {
            LoadMoreViewHolder(getView(parent, viewType))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!isLoadMoreLoading) {
            VIEW_TYPE_ITEM
        } else {
            VIEW_TYPE_LOAD_MORE
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Review>, position: Int) {
        if (datas.isNotEmpty()) {
            if (holder is ReviewViewHolder) holder.bind(data = datas[position])
        }
    }

    override fun getItemResourceLayout(viewType: Int): Int {
        return if (viewType == VIEW_TYPE_ITEM) {
            R.layout.item_review
        } else {
            R.layout.item_review_loading
        }
    }

    public override fun setLoadMoreProgress(isProgress: Boolean) {
        isLoadMoreLoading = isProgress
        notifyDataSetChanged()
    }


    inner class ReviewViewHolder(itemView: View) : BaseViewHolder<Review>(itemView) {
        val binding = ItemReviewBinding.bind(itemView)
        override fun bind(data: Review) {
            binding.apply {
                tvName.text = data.author
                tvComment.text = data.content

                if (isPreview){
                    tvComment.maxLines = 3
                }
            }
        }
    }

    inner class LoadMoreViewHolder(
        view: View
    ) : BaseViewHolder<Review>(view) {
        override fun bind(data: Review) {}
    }
}