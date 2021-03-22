package com.example.coolerthanyou

import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
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

    private var actorTypeFace: Typeface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Activity should be non-null after attach
        application = requireActivity().applicationContext as BaseApplication
    }

    /**
     * Sets the default fonts of AlertDialog elements to Actor
     * This MUST be called after [AlertDialog.show]
     *
     * @return  Returns the modified [AlertDialog] for chaining
     */
    protected fun AlertDialog.setDefaults(): AlertDialog {
        if (actorTypeFace == null) {
            actorTypeFace = ResourcesCompat.getFont(requireContext(), R.font.actor)
        }
        // title is already in Actor, so skip
        findViewById<TextView>(android.R.id.message)?.typeface = actorTypeFace
        getButton(AlertDialog.BUTTON_NEGATIVE)?.typeface = actorTypeFace
        getButton(AlertDialog.BUTTON_NEUTRAL)?.typeface = actorTypeFace
        getButton(AlertDialog.BUTTON_POSITIVE)?.typeface = actorTypeFace

        return this
    }
}