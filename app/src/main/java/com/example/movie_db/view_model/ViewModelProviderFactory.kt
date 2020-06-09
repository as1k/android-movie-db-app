package com.example.movie_db.view_model

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import android.content.Context

class ViewModelProviderFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java).newInstance(context)
    }
}
