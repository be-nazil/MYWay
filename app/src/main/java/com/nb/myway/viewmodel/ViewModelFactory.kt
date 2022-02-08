package com.nb.myway.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nb.myway.repository.LoadDataRepository
import java.io.InputStream

class ViewModelFactory(private val loadDataRepository: LoadDataRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoadDataViewModel(loadDataRepository) as T
    }
}