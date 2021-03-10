package com.example.coolerthanyou

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.coolerthanyou.dagger.ViewModelFactory
import com.example.coolerthanyou.log.ILogger
import javax.inject.Inject

/**
 * Base [AppCompatActivity] for all activities in this project.
 */
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    protected lateinit var logger: ILogger

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private var actorTypeFace: Typeface? = null

    /**
     * Sets the default fonts of AlertDialog elements to Actor
     * This MUST be called after [AlertDialog.show]
     *
     * @return  Returns the modified [AlertDialog] for chaining
     */
    protected fun AlertDialog.setDefaults(): AlertDialog {
        if (actorTypeFace == null) {
            actorTypeFace = ResourcesCompat.getFont(this@BaseActivity, R.font.actor)
        }
        // title is already in Actor, so skip
        findViewById<TextView>(android.R.id.message)?.typeface = actorTypeFace
        getButton(AlertDialog.BUTTON_NEGATIVE)?.typeface = actorTypeFace
        getButton(AlertDialog.BUTTON_NEUTRAL)?.typeface = actorTypeFace
        getButton(AlertDialog.BUTTON_POSITIVE)?.typeface = actorTypeFace

        return this
    }

    // from https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside/28939113#28939113
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // lose focus on taps outside of edit texts if current focus is an edit text
        if (ev.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()

                    //hide keyboard
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}