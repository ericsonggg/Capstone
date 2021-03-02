package com.example.coolerthanyou.ui.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R

class DetailsFragment : BaseFragment() {

    private val _detailsViewModel: DetailsViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        application.appComponent.mainComponent().create().inject(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_details, container, false)
        val textView: TextView = root.findViewById(R.id.text_details)
        _detailsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
