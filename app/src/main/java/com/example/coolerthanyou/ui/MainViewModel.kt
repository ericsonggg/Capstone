package com.example.coolerthanyou.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coolerthanyou.BaseViewModel
import com.example.coolerthanyou.ui.slideshow.IFeedbackProvider
import javax.inject.Inject

class MainViewModel @Inject constructor() : BaseViewModel(), IFeedbackProvider {
    private var _boxValue = MutableLiveData<Int>().apply {
        value = 0
    }

    override fun getBoxValue(): LiveData<Int> = _boxValue
    override fun setBoxValue(value: Int) {
        _boxValue = MutableLiveData<Int>(value)
    }
}