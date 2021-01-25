package com.lianda.movies.utils.extentions

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.lianda.movies.R
import com.lianda.movies.utils.custom.CustomMultiStateView


fun CustomMultiStateView.showLoadingView(){
    this.viewState = CustomMultiStateView.ViewState.LOADING
}

fun CustomMultiStateView.showEmptyView(icon:Int? = null, title:String? = null, message:String? = null, action:String? = null, actionListener:(()->Unit)? = null){
    this.viewState = CustomMultiStateView.ViewState.EMPTY

    this.getView(CustomMultiStateView.ViewState.EMPTY)?.apply{
        val imgIcon = findViewById<ImageView>(R.id.imgIcon)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val btnAction = findViewById<Button>(R.id.btnAction)

        if (icon != null) {
            imgIcon.setImageResource(icon)
        }
        if (title != null) {
            tvTitle.text = title
        }
        if (message != null) {
            tvMessage.text = message
        }
        if (action != null) {
            btnAction.text = action
            btnAction.visible()
        }else{
            btnAction.gone()
        }

        btnAction.onSingleClickListener { actionListener?.invoke() }
    }
}

fun CustomMultiStateView.showErrorView(icon:Int? = null, title:String? = null, message:String? = null, action:String? = null, actionListener:(()->Unit)? = null){
    this.viewState = CustomMultiStateView.ViewState.ERROR

    this.getView(CustomMultiStateView.ViewState.ERROR)?.apply{
        val imgIcon = findViewById<ImageView>(R.id.imgIcon)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val btnAction = findViewById<Button>(R.id.btnAction)

        if (icon != null) {
            imgIcon.setImageResource(icon)
        }
        if (title != null) {
            tvTitle.text = title
        }
        if (message != null) {
            tvMessage.text = message
        }
        if (action != null) {
            btnAction.text = action
            btnAction.visible()
            btnAction.onSingleClickListener { actionListener?.invoke() }
        }
    }
}

fun CustomMultiStateView.showContentView(){
    this.viewState = CustomMultiStateView.ViewState.CONTENT
}