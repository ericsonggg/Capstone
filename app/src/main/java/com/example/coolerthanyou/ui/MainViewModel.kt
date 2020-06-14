package com.example.coolerthanyou.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coolerthanyou.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : BaseViewModel() {
    private var _boxValue = MutableLiveData<Int>().apply {
        value = 0
    }

    fun getBoxValue(): LiveData<Int> = _boxValue
    fun setBoxValue(value: MutableLiveData<Int>) {
        _boxValue = value
    }
}