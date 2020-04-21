package com.example.coolerthanyou

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){
    protected lateinit var root: View

    fun findViewById(id: Int) : View {
        return root.findViewById(id)
    }
}