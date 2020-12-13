package com.example.coolerthanyou

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.example.coolerthanyou.dagger.ViewModelFactory
import com.example.coolerthanyou.log.ILogger
import javax.inject.Inject

/**
 * Base [Fragment] for all fragments in this project
 */
abstract class BaseFragment : Fragment() {

    @Inject
    internal lateinit var logger: ILogger

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory   // Use this when instantiating new ViewModels for fragments
    protected lateinit var application: BaseApplication
    protected lateinit var root: View

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Activity should be non-null after attach
        application = requireActivity().applicationContext as BaseApplication
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