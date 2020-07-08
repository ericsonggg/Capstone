package com.example.coolerthanyou.ui.slideshow

import androidx.lifecycle.LiveData

interface IFeedbackProvider {
    fun getBoxValue(): LiveData<Int>
    fun setBoxValue(value: Int)
}