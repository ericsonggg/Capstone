package com.example.coolerthanyou

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Base [Fragment] for all fragments in this project
 */
abstract class BaseFragment : Fragment() {

    protected lateinit var application: BaseApplication
    protected lateinit var root: View

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Activity should be non-null after attach
        application = activity!!.applicationContext as BaseApplication
    }

    /**
     * Shortcut for [View.findViewById]
     *
     * @param id    Resource id
     * @return      Appropriate view
     */
    fun findViewById(id: Int): View {
        return root.findViewById(id)
    }
}