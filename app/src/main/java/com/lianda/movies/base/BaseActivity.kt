package com.lianda.movies.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity :AppCompatActivity(){

    var layout:View? = null

    abstract fun onInflateView()
    abstract fun onPreparation()
    abstract fun onIntent()
    abstract fun onUi()
    abstract fun onAction()
    abstract fun onObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInflateView()
        layout?.let { setContentView(it) }

        onPreparation()
        onIntent()
        onUi()
        onAction()
        onObserver()
    }

    fun setupToolbar(toolbar: Toolbar, title: String, isBack: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = title
            it.setDisplayHomeAsUpEnabled(isBack)
        }
    }
}